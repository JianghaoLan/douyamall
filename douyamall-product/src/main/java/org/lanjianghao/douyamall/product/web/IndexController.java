package org.lanjianghao.douyamall.product.web;

import org.lanjianghao.douyamall.product.entity.CategoryEntity;
import org.lanjianghao.douyamall.product.service.CategoryService;
import org.lanjianghao.douyamall.product.vo.Catalog2Vo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
public class IndexController {

    @Autowired
    CategoryService categoryService;

    @GetMapping({"/", "index.html"})
    public String index(Model model) {
        List<CategoryEntity> categoryEntities = categoryService.listTopLevelCategories();
        model.addAttribute("categories", categoryEntities);

        return "index";
    }

    @ResponseBody
    @GetMapping("/index/json/catalog.json")
    public Map<String, List<Catalog2Vo>> getCatalogJson() {
        return categoryService.getCatalogJson();
    }
}
