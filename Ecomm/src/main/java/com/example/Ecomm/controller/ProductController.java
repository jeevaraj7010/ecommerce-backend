package com.example.Ecomm.controller;

import com.example.Ecomm.entity.Product;
import com.example.Ecomm.service.ProductService;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    // ✅ Get All Products (supports optional pagination, default 12 per page)
    @GetMapping
    public Object getProducts(@RequestParam(required = false) Integer page,
                              @RequestParam(required = false) Integer size) {
        if (page != null) {
            int pageSize = (size != null) ? size : 12;
            return productService.getAllProductsPaged(PageRequest.of(page, pageSize));
        }
        return productService.getAllProducts();
    }

    // ✅ 🔥 Get Product By ID (IMPORTANT FIX)
    @GetMapping("/{id}")
    public Product getProductById(@PathVariable Long id) {
        return productService.getProductById(id);
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

    // ✅ Get Products By Category (supports optional pagination, default 12 per page)
    @GetMapping("/category/{category}")
    public Object getByCategory(@PathVariable String category,
                                @RequestParam(required = false) Integer page,
                                @RequestParam(required = false) Integer size) {
        if (page != null) {
            int pageSize = (size != null) ? size : 12;
            return productService.getByCategoryPaged(category, PageRequest.of(page, pageSize));
        }
        return productService.getByCategory(category);
    }
}