package com.jeetp.billingservice;

import com.jeetp.billingservice.entities.Bill;
import com.jeetp.billingservice.entities.ProductItem;
import com.jeetp.billingservice.feign.CustomerRestClient;
import com.jeetp.billingservice.feign.ProductItemRestClient;
import com.jeetp.billingservice.model.Customer;
import com.jeetp.billingservice.model.Product;
import com.jeetp.billingservice.repository.BillRepository;
import com.jeetp.billingservice.repository.ProductItemRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.hateoas.PagedModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Random;

@SpringBootApplication
@EnableFeignClients
public class BillingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(BillingServiceApplication.class, args);
    }

    @Bean
    CommandLineRunner start(BillRepository billRepository ,
                            ProductItemRepository productItemRepository ,
                            CustomerRestClient customerRestClient ,
                            ProductItemRestClient productItemRestClient) {
        return args -> {
            Customer customer = customerRestClient.getCustomerById(1L);
            Bill bill = billRepository.save(new Bill(null , new Date() , null , customer.getId() , null));
            PagedModel<Product> productPagedModel = productItemRestClient.pageProducts();
            productPagedModel.forEach(p -> {
                ProductItem productItem = new ProductItem();
                productItem.setProductID(p.getId());
                productItem.setPrice(p.getPrice());
                productItem.setQuantity(1+new Random().nextInt(100));
                productItem.setBill(bill);
                productItemRepository.save(productItem);
            });
        };
    }
}
