package com.example.Ecomm.controller;

import com.example.Ecomm.dto.ProductCustomerDTO;
import com.example.Ecomm.entity.Product;
import com.example.Ecomm.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "https://ecommerce-frontend-h3as.vercel.app")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // ✅ Add Product
    @PostMapping
    public Product addProduct(@RequestBody Product product) {
        return productService.saveProduct(product);
    }

    // ✅ Get All Products (supports optional pagination)
    @GetMapping
    public Object getProducts(@RequestParam(required = false) Integer page,
                              @RequestParam(required = false) Integer size) {
        if (page != null) {
            int pageSize = (size != null) ? size : 12;
            Page<Product> paged = productService.getAllProductsPaged(PageRequest.of(page, pageSize));
            return paged.map(ProductCustomerDTO::fromEntity);
        }
        return productService.getAllProducts().stream()
                .map(ProductCustomerDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // ✅ Get Product By ID (Returns ProductCustomerDTO with variants & gallery images)
    @GetMapping("/{id}")
    public ProductCustomerDTO getProductById(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        return ProductCustomerDTO.fromEntity(product);
    }

    // ✅ Delete Product
    @DeleteMapping("/{id}")
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return "Product Deleted Successfully";
    }

    // ✅ Quick Stock Update for Admin Inventory
    @PutMapping("/{id}/stock")
    public Product updateStock(@PathVariable Long id, @RequestBody java.util.Map<String, Integer> body) {
        int quantity = body.getOrDefault("quantity", 0);
        return productService.updateStock(id, quantity);
    }

    // ✅ Get Products By Category (supports optional pagination)
    @GetMapping("/category/{category}")
    public Object getByCategory(@PathVariable String category,
                                @RequestParam(required = false) Integer page,
                                @RequestParam(required = false) Integer size) {
        if (page != null) {
            int pageSize = (size != null) ? size : 12;
            Page<Product> paged = productService.getByCategoryPaged(category, PageRequest.of(page, pageSize));
            return paged.map(ProductCustomerDTO::fromEntity);
        }
        return productService.getByCategory(category).stream()
                .map(ProductCustomerDTO::fromEntity)
                .collect(Collectors.toList());
    }
}