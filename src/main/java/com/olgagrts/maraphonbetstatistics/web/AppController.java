package com.olgagrts.maraphonbetstatistics.web;

import com.olgagrts.maraphonbetstatistics.core.Match;
import com.olgagrts.maraphonbetstatistics.core.ReportGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.List;

@Controller
public class AppController {

    @Autowired
    private ReportGenerator reportGenerator;

    @RequestMapping("/getReport")
    private String listProducts(Model model) throws IOException {
        List<Match> matchList = null;
        matchList = reportGenerator.makeReport();
        model.addAttribute("matches", matchList);
        return "matches";
    }

}
