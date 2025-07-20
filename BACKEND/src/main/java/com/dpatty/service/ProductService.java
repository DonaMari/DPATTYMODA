package com.dpatty.service;

import com.dpatty.dto.product.ProductCreateRequest;
import com.dpatty.dto.product.ProductResponse;
import com.dpatty.model.Product;
import com.dpatty.model.ProductVariant;
import com.dpatty.repository.ProductRepository;
import com.dpatty.repository.ProductVariantRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductVariantRepository variantRepository;

    @Autowired
    private ModelMapper modelMapper;

    public Page<ProductResponse> getAllProducts(Pageable pageable) {
        Page<Product> products = productRepository.findByIsActiveTrueOrderByCreatedAtDesc(pageable);
        return products.map(product -> modelMapper.map(product, ProductResponse.class));
    }

    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        return modelMapper.map(product, ProductResponse.class);
    }

    public Page<ProductResponse> searchProducts(String search, Pageable pageable) {
        Page<Product> products = productRepository.findBySearchTerm(search, pageable);
        return products.map(product -> modelMapper.map(product, ProductResponse.class));
    }

    @Transactional
    public ProductResponse createProduct(ProductCreateRequest request) {
        // Validate SKU uniqueness
        if (productRepository.existsBySku(request.getSku())) {
            throw new RuntimeException("SKU ya existe: " + request.getSku());
        }

        Product product = modelMapper.map(request, Product.class);
        product.setIsActive(true);
        
        Product savedProduct = productRepository.save(product);

        // Create default variant if variants are not specified
        if (request.getVariants() == null || request.getVariants().isEmpty()) {
            ProductVariant defaultVariant = new ProductVariant();
            defaultVariant.setProduct(savedProduct);
            defaultVariant.setSku(savedProduct.getSku() + "-DEFAULT");
            defaultVariant.setStockQuantity(request.getInitialStock() != null ? request.getInitialStock() : 0);
            defaultVariant.setIsActive(true);
            
            variantRepository.save(defaultVariant);
        }

        return modelMapper.map(savedProduct, ProductResponse.class);
    }

    @Transactional
    public ProductResponse updateProduct(Long id, ProductCreateRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        // Update product fields
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setComparePrice(request.getComparePrice());
        // Add other fields as needed

        Product savedProduct = productRepository.save(product);
        return modelMapper.map(savedProduct, ProductResponse.class);
    }

    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        
        product.setIsActive(false);
        productRepository.save(product);
    }

    private String generateUniqueSku() {
        String sku;
        do {
            sku = "PROD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } while (productRepository.existsBySku(sku));
        return sku;
    }
}