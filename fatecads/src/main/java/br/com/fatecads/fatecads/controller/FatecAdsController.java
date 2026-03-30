package br.com.fatecads.fatecads.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/fatecads")
public class FatecAdsController {
    
    @GetMapping
    public String index(Model model){
        return"index";
    }
}
