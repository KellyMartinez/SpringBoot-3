package com.martinez.springboot.controlers;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.martinez.springboot.dtos.ProductRecordDto;
import com.martinez.springboot.models.ProductModel;
import com.martinez.springboot.repositories.ProductRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import jakarta.validation.Valid;

@RestController
@RequestMapping( produces = { "application/json" })
@Tag(name = "SpringBoot")
public class ProductController {

	@Autowired
	ProductRepository productRepository;

	@Operation(summary = "Cadastrar um novo produto", method = "POST")
	@ApiResponses(value = { @ApiResponse(responseCode = "201", description = "Produto cadastrado com sucesso!"),
							@ApiResponse(responseCode = "400", description = "Parâmetros inválidos"),
							@ApiResponse(responseCode = "500", description = "Erro ao realizar a operação"), })

	@PostMapping(value = "/products", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ProductModel> saveProduct(@RequestBody @Valid ProductRecordDto productRecordDto) {
		var productModel = new ProductModel();
		BeanUtils.copyProperties(productRecordDto, productModel);
		return ResponseEntity.status(HttpStatus.CREATED).body(productRepository.save(productModel));

	}

	@Operation(summary = "Buscar todos os produtos cadastrados", method = "GET")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "OK!"),
							@ApiResponse(responseCode = "404", description = "Nenhum registro encontrado"),
							@ApiResponse(responseCode = "500", description = "Erro ao realizar a operação"), })

	@GetMapping(value = "/products")
	public ResponseEntity<List<ProductModel>> getAllProducts() {
		List<ProductModel> productList = productRepository.findAll();
		if (!productList.isEmpty()) {
			for (ProductModel product : productList) {
				UUID id = product.getIdProduct();
				product.add(linkTo(methodOn(ProductController.class).getOneProduct(id)).withSelfRel());
			}
		}
		return ResponseEntity.status(HttpStatus.OK).body(productList);
	}

	@Operation(summary = "Buscar produto através do id", method = "GET")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "OK!"),
							@ApiResponse(responseCode = "404", description = "Product not found"),
							@ApiResponse(responseCode = "500", description = "Erro ao realizar a operação"),
							@ApiResponse(responseCode = "415", description = "Unsupported Media Type"),
						  })

	@GetMapping(value = "/products/{id}")
	public ResponseEntity<Object> getOneProduct(@PathVariable(value = "id") UUID id) {
		Optional<ProductModel> product0 = productRepository.findById(id);
		if (product0.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
		}
		product0.get().add(linkTo(methodOn(ProductController.class).getAllProducts()).withRel("Products List"));
		return ResponseEntity.status(HttpStatus.OK).body(product0.get());
	}

	@Operation(summary = "Alterar um produto", method = "PUT")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "OK!"),
							@ApiResponse(responseCode = "404", description = "Product not found"),
							@ApiResponse(responseCode = "500", description = "Erro ao realizar a operação"), })

	@PutMapping(value = "/products/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> updateProduct(@PathVariable(value = "id") UUID id,
			@RequestBody @Valid ProductRecordDto productRecordDto) {
		Optional<ProductModel> product0 = productRepository.findById(id);
		if (product0.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
		}
		var productModel = product0.get();
		BeanUtils.copyProperties(productRecordDto, productModel);
		return ResponseEntity.status(HttpStatus.OK).body(productRepository.save(productModel));

	}
	@Operation(summary = "Deletar um produto", method = "DELETE")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "OK!"),
							@ApiResponse(responseCode = "404", description = "Product not found"),
							@ApiResponse(responseCode = "500", description = "Erro ao realizar a operação"), })

	@DeleteMapping(value = "/products/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> deleteProduct(@PathVariable(value = "id") UUID id) {

		Optional<ProductModel> product0 = productRepository.findById(id);

		if (product0.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
		}
		productRepository.delete(product0.get());
		return ResponseEntity.status(HttpStatus.OK).body("Product deleted sucessfully");

	}
}
