package org.lanjianghao.douyamall.search.repository;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.bulk.BulkResponseItem;
import lombok.extern.slf4j.Slf4j;
import org.lanjianghao.douyamall.search.entity.ProductEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class ProductRepository {
    @Autowired
    ElasticsearchClient esClient;

    public boolean saveProducts(List<ProductEntity> productEntities) throws IOException {
        BulkRequest.Builder br = new BulkRequest.Builder();

        for (ProductEntity product : productEntities) {
            br.operations(op -> op
                    .index(idx -> idx
                            .index("product")
                            .id(product.getSkuId().toString())
                            .document(product)
                    )
            );
        }

        BulkResponse result = esClient.bulk(br.build());

        if (result.errors()) {
            List<String> ids = result.items().stream().map(BulkResponseItem::id).collect(Collectors.toList());

            log.error("商品保存发生错误，SkuIds:{}", ids);
            for (BulkResponseItem item: result.items()) {
                if (item.error() != null) {
                    log.error("{}:{}", item.id(), item.error().reason());
                }
            }
            return false;
        }
        return true;
    }
}
