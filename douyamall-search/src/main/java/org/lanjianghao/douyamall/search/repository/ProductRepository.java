package org.lanjianghao.douyamall.search.repository;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregate;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.bulk.BulkResponseItem;
import co.elastic.clients.elasticsearch.core.search.Highlight;
import co.elastic.clients.elasticsearch.core.search.HighlightField;
import co.elastic.clients.json.JsonData;
import co.elastic.clients.util.ObjectBuilder;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.lanjianghao.common.constant.SearchConstant;
import org.lanjianghao.common.utils.R;
import org.lanjianghao.douyamall.search.entity.ProductEntity;
import org.lanjianghao.douyamall.search.feign.ProductFeignService;
import org.lanjianghao.douyamall.search.vo.AttrNameVo;
import org.lanjianghao.douyamall.search.vo.BrandNameVo;
import org.lanjianghao.douyamall.search.vo.ProductSearchParam;
import org.lanjianghao.douyamall.search.vo.ProductSearchResult;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class ProductRepository {
    @Autowired
    private ElasticsearchClient esClient;

    public boolean saveProducts(List<ProductEntity> productEntities) throws IOException {
        BulkRequest.Builder br = new BulkRequest.Builder();

        for (ProductEntity product : productEntities) {
            br.operations(op -> op
                    .index(idx -> idx
                            .index(SearchConstant.PRODUCT_INDEX)
                            .id(product.getSkuId().toString())
                            .document(product)
                    )
            );
        }

        BulkResponse result = esClient.bulk(br.build());

        if (result.errors()) {
            List<String> ids = result.items().stream().map(BulkResponseItem::id).collect(Collectors.toList());

            log.error("商品保存发生错误，SkuIds:{}", ids);
            for (BulkResponseItem item : result.items()) {
                if (item.error() != null) {
                    log.error("{}:{}", item.id(), item.error().reason());
                }
            }
            return false;
        }
        return true;
    }

    private ObjectBuilder<SortOptions> buildSort(SortOptions.Builder b, String sort) {
        String[] split = sort.split("_", 2);
        SortOrder order = split[1].equalsIgnoreCase("desc") ? SortOrder.Desc : SortOrder.Asc;
        return b.field(s -> s
                .field(split[0])
                .order(order));
    }

    private ObjectBuilder<Highlight> buildHighlightSkuTitle(Highlight.Builder b) {
        return b
                .fields("skuTitle", HighlightField.of(hb -> hb))
                .preTags("<b style='color:red'>")
                .postTags("</b>");
    }

    private ObjectBuilder<Aggregation> buildBrandAgg(Aggregation.Builder a) {
        return a
                .terms(t -> t
                        .field("brandId")
                        .size(SearchConstant.SEARCH_PRODUCT_TERMS_AGG_SIZE))
                .aggregations("brand_name_agg", sa -> sa
                        .terms(st -> st
                                .field("brandName")
                                .size(1)))
                .aggregations("brand_img_agg", sa -> sa
                        .terms(st -> st
                                .field("brandImg")
                                .size(1)));
    }

    private ObjectBuilder<Aggregation> buildCatalogAgg(Aggregation.Builder a) {
        return a
                .terms(t -> t
                        .field("catalogId")
                        .size(SearchConstant.SEARCH_PRODUCT_TERMS_AGG_SIZE))
                .aggregations("catalog_name_agg", sa -> sa
                        .terms(t -> t
                                .field("catalogName")
                                .size(1)));
    }

    private ObjectBuilder<Aggregation> buildAttrAgg(Aggregation.Builder a) {
        return a
                .nested(na -> na
                        .path("attrs"))
                .aggregations("attr_agg", na -> na
                        .terms(t -> t
                                .field("attrs.attrId")
                                .size(SearchConstant.SEARCH_PRODUCT_TERMS_AGG_SIZE))
                        .aggregations("attr_name_agg", sa -> sa
                                .terms(t -> t
                                        .field("attrs.attrName")
                                        .size(1)))
                        .aggregations("attr_value_agg", sa -> sa
                                .terms(t -> t
                                        .field("attrs.attrValue")
                                        .size(SearchConstant.SEARCH_PRODUCT_TERMS_AGG_SIZE))));
    }

    private SearchRequest.Builder buildSearchRequest(SearchRequest.Builder builder, ProductSearchParam param) {
        builder.index(SearchConstant.PRODUCT_INDEX);

        BoolQuery.Builder boolQueryBuilder = new BoolQuery.Builder();

        //skuTitle
        if (param.getKeyword() != null && StringUtils.hasLength(param.getKeyword().trim())) {
            boolQueryBuilder.must(q -> q
                    .match(m -> m
                            .field("skuTitle")
                            .query(param.getKeyword().trim())));
        }

        //catalogId
        if (param.getCatalogId() != null) {
            boolQueryBuilder.filter(q -> q
                    .term(t -> t
                            .field("catalogId")
                            .value(param.getCatalogId())));
        }

        //brandIds
        if (!CollectionUtils.isEmpty(param.getBrandIds())) {
            List<FieldValue> terms = param.getBrandIds().stream().map(FieldValue::of).collect(Collectors.toList());
            boolQueryBuilder.filter(q -> q
                    .terms(t -> t
                            .field("brandId")
                            .terms(v -> v.value(terms))));
        }

        //hasStock
        if (param.getHasStock() != null) {
            boolQueryBuilder.filter(q -> q
                    .term(t -> t
                            .field("hasStock")
                            .value(param.getHasStock() == 1)));
        }

        //skuPriceRange
        if (StringUtils.hasLength(param.getSkuPriceRange())) {
            String[] range = param.getSkuPriceRange().split("_", 2);

            boolQueryBuilder.filter(q -> q
                    .range(r -> {
                                r.field("skuPrice");
                                if (range[0].length() > 0) {
                                    r.gte(JsonData.of(range[0]));
                                }
                                if (range[1].length() > 0) {
                                    r.lte(JsonData.of(range[1]));
                                }
                                return r;
                            }
                    ));
        }

        //attrs
        if (!CollectionUtils.isEmpty(param.getAttrs())) {
            param.getAttrs().forEach(attr -> {
                String[] split = attr.split("_", 2);
                String attrId = split[0];
                String[] values = split[1].split(":");
                List<FieldValue> terms = Arrays.stream(values).map(FieldValue::of).collect(Collectors.toList());
                boolQueryBuilder.filter(q -> q
                        .nested(nq -> nq
                                .path("attrs")
                                .query(nqq -> nqq
                                        .bool(b -> b
                                                .filter(f -> f
                                                        .term(t -> t
                                                                .field("attrs.attrId")
                                                                .value(attrId)))
                                                .filter(f -> f
                                                        .terms(t -> t
                                                                .field("attrs.attrValue")
                                                                .terms(v -> v.value(terms))))))));
            });
        }

        //query
        builder.query(q -> q.bool(boolQueryBuilder.build()));

        //sort
        if (StringUtils.hasLength(param.getSort())) {
            builder.sort(sb -> buildSort(sb, param.getSort()));
        }

        //highlight
        builder.highlight(this::buildHighlightSkuTitle);

        //aggs
        builder
                .aggregations("brand_agg", this::buildBrandAgg)
                .aggregations("catalog_agg", this::buildCatalogAgg)
                .aggregations("attr_agg", this::buildAttrAgg);

        //page
        builder.from(param.getPageNum() == null ? 0 :
                (param.getPageNum() - 1) * SearchConstant.SEARCH_PRODUCT_PAGE_SIZE);
        builder.size(SearchConstant.SEARCH_PRODUCT_PAGE_SIZE);

        return builder;
    }

    private List<ProductSearchResult.Brand> getBrandsFromAgg(Aggregate brandAgg) {
        return brandAgg.lterms().buckets().array().stream().map(bucket -> {
            ProductSearchResult.Brand brand = new ProductSearchResult.Brand();
            brand.setBrandId(Long.parseLong(bucket.key()));

            Map<String, Aggregate> subAggs = bucket.aggregations();
            brand.setBrandName(subAggs.get("brand_name_agg").sterms().buckets().array().get(0).key().stringValue());
            brand.setBrandImg(subAggs.get("brand_img_agg").sterms().buckets().array().get(0).key().stringValue());
            return brand;
        }).collect(Collectors.toList());
    }

    private List<ProductSearchResult.Catalog> getCatalogsFromAgg(Aggregate catalogAgg) {
        return catalogAgg.lterms().buckets().array().stream().map(bucket -> {
            ProductSearchResult.Catalog catalog = new ProductSearchResult.Catalog();
            catalog.setCatalogId(Long.parseLong(bucket.key()));
            catalog.setCatalogName(bucket.aggregations().get("catalog_name_agg")
                    .sterms().buckets().array().get(0).key().stringValue());
            return catalog;
        }).collect(Collectors.toList());
    }

    private List<ProductSearchResult.Attr> getAttrsFromAgg(Aggregate attrAgg, List<Long> exclusion) {
        return attrAgg.nested().aggregations().get("attr_agg")
                .lterms().buckets().array().stream().map(bucket -> {
                    Map<String, Aggregate> subAggs = bucket.aggregations();
                    ProductSearchResult.Attr attr = new ProductSearchResult.Attr();
                    attr.setAttrId(Long.parseLong(bucket.key()));
                    attr.setAttrName(subAggs.get("attr_name_agg").sterms().buckets().array().get(0).key().stringValue());
                    attr.setAttrValues(subAggs.get("attr_value_agg").sterms().buckets().array().stream().map(
                            valuebucket -> valuebucket.key().stringValue()
                    ).collect(Collectors.toList()));
                    return attr;
                }).filter(attr -> !exclusion.contains(attr.getAttrId())).collect(Collectors.toList());
    }

    private List<Long> parseAttrIds(List<String> attrs) {
        if (CollectionUtils.isEmpty(attrs)) {
            return Collections.emptyList();
        }
        return attrs.stream().map(
                attr -> Long.parseLong(attr.split("_", 2)[0])
        ).collect(Collectors.toList());
    }

    public ProductSearchResult search(ProductSearchParam param) {
//        //debug dsl
//        SearchRequest.Builder builder = new SearchRequest.Builder();
//        String toString = buildSearchRequest(builder, param).build().toString();
//        System.out.println("DSL: " + toString);

        SearchResponse<ProductEntity> resp;
        try {
            //search
            resp = esClient.search(s -> buildSearchRequest(s, param), ProductEntity.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        ProductSearchResult result = new ProductSearchResult();

        //set total
        result.setProducts(resp.hits().hits().stream().map(hit -> {
            if (hit.highlight().containsKey("skuTitle")) {
                ProductEntity product = new ProductEntity();
                assert hit.source() != null;
                BeanUtils.copyProperties(hit.source(), product);
                product.setSkuTitle(hit.highlight().get("skuTitle").get(0));
                return product;
            }
            return hit.source();
        }).collect(Collectors.toList()));
        if (resp.hits().total() != null) {
            result.setTotal(resp.hits().total().value());
        }

        Map<String, Aggregate> aggs = resp.aggregations();
        //set brands
        result.setBrands(getBrandsFromAgg(aggs.get("brand_agg")));
        //set catalogs
        result.setCatalogs(getCatalogsFromAgg(aggs.get("catalog_agg")));
        //set attrs
        result.setAttrs(getAttrsFromAgg(aggs.get("attr_agg"), parseAttrIds(param.getAttrs())));

        //set totalPages
        result.setTotalPages((result.getTotal() + SearchConstant.SEARCH_PRODUCT_PAGE_SIZE - 1) /
                SearchConstant.SEARCH_PRODUCT_PAGE_SIZE);
        //set pageNum
        result.setPageNum(param.getPageNum() == null ? 1 : param.getPageNum());

        return result;
    }

}
