package com.interview.productservice.infrastructure.web;

import com.interview.productservice.api.ProductsApi;
import com.interview.productservice.api.model.CreateProductRequest;
import com.interview.productservice.api.model.ProductCollectionResponse;
import com.interview.productservice.api.model.ProductResponse;
import com.interview.productservice.api.model.UpdateProductRequest;
import com.interview.productservice.application.usecase.CreateProductUseCase;
import com.interview.productservice.application.usecase.DeleteProductUseCase;
import com.interview.productservice.application.usecase.GetProductUseCase;
import com.interview.productservice.application.usecase.ListProductsUseCase;
import com.interview.productservice.application.usecase.UpdateProductUseCase;
import com.interview.productservice.application.usecase.command.CreateProductCommand;
import com.interview.productservice.application.usecase.command.UpdateProductCommand;
import com.interview.productservice.domain.model.Product;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
public class ProductController implements ProductsApi {

    private final CreateProductUseCase createProductUseCase;
    private final GetProductUseCase getProductUseCase;
    private final ListProductsUseCase listProductsUseCase;
    private final UpdateProductUseCase updateProductUseCase;
    private final DeleteProductUseCase deleteProductUseCase;

    public ProductController(CreateProductUseCase createProductUseCase,
                             GetProductUseCase getProductUseCase,
                             ListProductsUseCase listProductsUseCase,
                             UpdateProductUseCase updateProductUseCase,
                             DeleteProductUseCase deleteProductUseCase) {
        this.createProductUseCase = createProductUseCase;
        this.getProductUseCase = getProductUseCase;
        this.listProductsUseCase = listProductsUseCase;
        this.updateProductUseCase = updateProductUseCase;
        this.deleteProductUseCase = deleteProductUseCase;
    }

    @Override
    public ResponseEntity<ProductResponse> createProduct(CreateProductRequest createProductRequest) {
        Product product = createProductUseCase.execute(new CreateProductCommand(
                createProductRequest.getName(),
                createProductRequest.getDescription(),
                toBigDecimal(createProductRequest.getPrice())));
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(product));
    }

    @Override
    public ResponseEntity<Void> deleteProduct(UUID productId) {
        deleteProductUseCase.execute(productId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<ProductResponse> getProduct(UUID productId) {
        return ResponseEntity.ok(toResponse(getProductUseCase.execute(productId)));
    }

    @Override
    public ResponseEntity<ProductCollectionResponse> listProducts() {
        ProductCollectionResponse response = new ProductCollectionResponse();
        response.setProducts(listProductsUseCase.execute().stream().map(this::toResponse).toList());
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ProductResponse> updateProduct(UUID productId, UpdateProductRequest updateProductRequest) {
        Product product = updateProductUseCase.execute(productId, new UpdateProductCommand(
                updateProductRequest.getName(),
                updateProductRequest.getDescription(),
                toBigDecimal(updateProductRequest.getPrice())));
        return ResponseEntity.ok(toResponse(product));
    }

    private ProductResponse toResponse(Product product) {
        return new ProductResponse()
                .id(product.id())
                .name(product.name())
                .description(product.description())
                .price(product.price().doubleValue())
                .currency(ProductResponse.CurrencyEnum.EUR)
                .createdAt(product.createdAt())
                .updatedAt(product.updatedAt());
    }

    private BigDecimal toBigDecimal(Double value) {
        return BigDecimal.valueOf(value);
    }
}