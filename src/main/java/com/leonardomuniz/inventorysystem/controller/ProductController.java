package com.leonardomuniz.inventorysystem.controller;

import com.leonardomuniz.inventorysystem.dto.ProductDTO;
import com.leonardomuniz.inventorysystem.model.Product;
import com.leonardomuniz.inventorysystem.service.ProductService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/products")
@Hidden
@RequiredArgsConstructor
public class ProductController {

    private final ProductService service;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("products", service.findAll());
        return "products/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("productDTO", new ProductDTO(null, "", "", null, null, "", null));
        model.addAttribute("productId", 0L);
        return "products/form";
    }

    @ApiResponse(responseCode = "302", description = "Redirects to dashboard on success")
    @PostMapping
    public String create(@Valid @ModelAttribute("productDTO") ProductDTO dto,
                         BindingResult result,
                         Model model,
                         RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("productId", 0L);
            return "products/form";
        }

        service.create(dto);
        redirectAttributes.addFlashAttribute("successMessage", "Product created successfully!");
        return "redirect:/products";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Product product = service.findById(id);
        model.addAttribute("productDTO", ProductDTO.fromEntity(product));
        model.addAttribute("productId", id);
        return "products/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("productDTO") ProductDTO dto,
                         BindingResult result,
                         Model model,
                         RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("productId", id);
            return "products/form";
        }

        service.update(id, dto);
        redirectAttributes.addFlashAttribute("successMessage", "Product updated successfully!");
        return "redirect:/products";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        service.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Product deleted successfully!");
        return "redirect:/products";
    }
}
