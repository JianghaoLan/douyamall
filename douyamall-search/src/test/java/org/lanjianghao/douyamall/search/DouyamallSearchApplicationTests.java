package org.lanjianghao.douyamall.search;

import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.junit.jupiter.api.Test;
//import org.junit.runner.RunWith;
import org.lanjianghao.douyamall.search.entity.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.*;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.stream.Collectors;

//@RunWith(SpringRunner.class)
@SpringBootTest
public class DouyamallSearchApplicationTests {

    @Autowired
    private RestHighLevelClient client;

    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    @Autowired
    private DocumentOperations documentOperations;

    @Test
    public void testESIndex() {
        Account account = new Account();
        account.setAccountNumber(0);
        account.setAge(18);
        account.setBalance(1000);
        account.setFirstname("lan");
        account.setLastname("Jianghao");
//        elasticsearchOperations.index(new IndexQueryBuilder().withId("0").withObject(account).build(),
//                IndexCoordinates.of("account"));
        elasticsearchOperations.save(account);
        SearchHits<Account> accounts = elasticsearchOperations.search(
                new CriteriaQuery(new Criteria()), Account.class, IndexCoordinates.of("account"));
        List<SearchHit<Account>> accountList = accounts.stream().collect(Collectors.toList());
        System.out.println(accountList);
    }

    @Test
    public void testESSearch() {
//        System.out.println(client);
//        System.out.println(elasticsearchOperations);
        SearchHits<Account> accounts = elasticsearchOperations.search(
                new CriteriaQuery(new Criteria("balance").lessThan(6000)), Account.class);
        List<SearchHit<Account>> accountList = accounts.stream().collect(Collectors.toList());
        System.out.println(accountList);
    }

    @Test
    void contextLoads() {


    }
}
