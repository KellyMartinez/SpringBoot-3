package com.martinez.springboot.dtos;

import java.math.BigDecimal;

import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import com.martinez.springboot.models.ProductModel;
import com.martinez.springboot.repositories.ProductRepository;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProductRecordDto(@NotBlank String name, @NotNull BigDecimal value) {


}
