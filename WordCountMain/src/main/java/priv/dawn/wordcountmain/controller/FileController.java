package priv.dawn.wordcountmain.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import priv.dawn.wordcountmain.pojo.vo.FileTextVO;
import priv.dawn.wordcountmain.service.FileStorageService;

@RestController
@RequestMapping("/demo/v1/api/file")
public class FileController {

    @Autowired
    private FileStorageService fileStorageService;

    // TODO: 2024/5/9 这都写的什么乱七八糟的东西, 后面记得改 
    @PostMapping("/upload")
    public int uploadFileText(@RequestBody FileTextVO fileTextVO) {
        return fileStorageService.saveFile(fileTextVO);
    }

    @GetMapping("/read/{id}")
    public String getFile(@PathVariable("id") int id){
        return fileStorageService.getContext(id);
    }

}
