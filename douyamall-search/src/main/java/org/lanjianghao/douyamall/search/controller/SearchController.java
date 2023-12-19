package org.lanjianghao.douyamall.search.controller;

import org.lanjianghao.douyamall.search.service.EsProductService;
import org.lanjianghao.douyamall.search.vo.ProductSearchParam;
import org.lanjianghao.douyamall.search.vo.ProductSearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SearchController {

    @Autowired
    EsProductService esProductService;

    @GetMapping("/list.html")
    public String listPage(@Validated ProductSearchParam searchParam, Model model) {

        ProductSearchResult result = esProductService.search(searchParam);
        model.addAttribute("result", result);

        return "list";
    }
}
