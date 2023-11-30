package org.lanjianghao.douyamall.search.entity;

import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Document;

@Data
@Document(indexName = "account")
public class Account {
    int accountNumber;
    int balance;
    String firstname, lastname;
    int age;
}
