package priv.dawn.wordcount.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import priv.dawn.wordcount.pojo.vo.TextFileVo;
import priv.dawn.wordcount.service.FileStorageService;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

@RestController
@RequestMapping("/v2/api/file")
public class FileController {

    @Resource
    private FileStorageService fileStorageService;

    @GetMapping("/download/{fileId}")
    public void download(@PathVariable int fileId, HttpServletResponse response) throws IOException {

        // TODO 这个接口后面要改
        TextFileVo textFileVo = fileStorageService.getFile(fileId);

        String context = textFileVo.getContext();
        if (Objects.isNull(context)) {
            response.sendError(405, "File Error");
            return;
        }

        try {
            InputStream inputStream = new ByteArrayInputStream(context.getBytes(StandardCharsets.UTF_8));
            response.reset();
            response.setContentType("application/octet-stream");
            response.addHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(textFileVo.getFilename(), "UTF-8"));
            ServletOutputStream outputStream = response.getOutputStream();
            byte[] b = new byte[1024];
            int len;
            //从输入流中读取一定数量的字节，并将其存储在缓冲区字节数组中，读到末尾返回-1
            while ((len = inputStream.read(b)) > 0) {
                outputStream.write(b, 0, len);
            }
            inputStream.close();
        } catch (Exception e) {
            response.sendError(405, "File Error");
        }
    }

    @PostMapping("/upload")
    public String upload(@RequestParam("file") MultipartFile file) {
        char[] buffer = new char[1024];
        StringBuilder builder = new StringBuilder();
        int len;
        try {
            InputStream inputStream = file.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            while ((len = reader.read(buffer)) > 0) {
                builder.append(buffer, 0, len);
            }
            String filename = file.getOriginalFilename();
            TextFileVo textFileVo = new TextFileVo(filename, builder.toString());
            fileStorageService.saveFile(textFileVo);
            return "Success : " + filename;
        } catch (Exception e) {
            return "Fail : "+e;
        }
    }

}
