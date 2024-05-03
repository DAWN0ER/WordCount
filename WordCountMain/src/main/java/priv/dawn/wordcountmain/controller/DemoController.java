package priv.dawn.wordcountmain.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import priv.dawn.wordcountmain.pojo.vo.FileTextVO;
import priv.dawn.wordcountmain.service.FileStorageService;

@RestController
@RequestMapping("/demo/v1/api")
public class DemoController {

    @Autowired
    private FileStorageService fileStorageService;

    @PostMapping("/upload")
    public int uploadFileText(@RequestBody FileTextVO fileTextVO) {
        try {
            return fileStorageService.saveFile(fileTextVO);
        } catch (Exception e) {
            return -1;
        }
    }

    @GetMapping("/file/{id}")
    public String getFile(@PathVariable("id") int id){
        return fileStorageService.getContext(id);
    }

}
