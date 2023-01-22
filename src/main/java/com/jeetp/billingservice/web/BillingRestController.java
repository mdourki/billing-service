package com.jeetp.billingservice.web;

import com.jeetp.billingservice.entities.Bill;
import com.jeetp.billingservice.feign.CustomerRestClient;
import com.jeetp.billingservice.feign.ProductItemRestClient;
import com.jeetp.billingservice.model.Customer;
import com.jeetp.billingservice.model.Product;
import com.jeetp.billingservice.repository.BillRepository;
import com.jeetp.billingservice.repository.ProductItemRepository;
import org.springframework.hateoas.PagedModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BillingRestController {
    public BillRepository billRepository;
    public ProductItemRepository productItemRepository;
    public CustomerRestClient customerRestClient;
    public ProductItemRestClient productItemRestClient;

    public BillingRestController(BillRepository billRepository, ProductItemRepository productItemRepository, CustomerRestClient customerRestClient, ProductItemRestClient productItemRestClient) {
        this.billRepository = billRepository;
        this.productItemRepository = productItemRepository;
        this.customerRestClient = customerRestClient;
        this.productItemRestClient = productItemRestClient;
    }

    @GetMapping(path = "/fullBill/{id}")
    public Bill getBill(@PathVariable Long id) {
        Bill bill = billRepository.findById(id).get();
        Customer customer = customerRestClient.getCustomerById(bill.getCustomerID());
        bill.setCustomer(customer);
        bill.getProductItems().forEach(pi -> {
            Product product = productItemRestClient.getProductById(pi.getProductID());
            pi.setProduct(product);
            pi.setProductName(product.getName());
        });
        return bill;
    }
}
