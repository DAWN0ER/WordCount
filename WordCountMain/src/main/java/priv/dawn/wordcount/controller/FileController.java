package priv.dawn.wordcount.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import priv.dawn.wordcount.pojo.vo.TextFileVo;
import priv.dawn.wordcount.service.TextFileService;

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
    private TextFileService textFileService;

    @GetMapping("/download/{fileUid}")
    public void download(@PathVariable int fileUid, HttpServletResponse response) throws IOException {

        TextFileVo textFileVo = textFileService.getFile(fileUid);

        if (Objects.isNull(textFileVo)) {
            response.sendError(404, "File Not Found");
            return;
        }

        String context = textFileVo.getContext();
        try {
            InputStream inputStream = new ByteArrayInputStream(context.getBytes(StandardCharsets.UTF_8));
            response.reset();
            response.setContentType("application/octet-stream");
            response.addHeader("Content-Disposition",
                    "attachment; filename=" + URLEncoder.encode(textFileVo.getFilename(), "UTF-8"));
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
            TextFileVo textFileVo = new TextFileVo();
            textFileVo.setFilename(filename);
            textFileVo.setContext(builder.toString());
            int fileUid = textFileService.updateFile(textFileVo);
            if (fileUid>0) {
                return "Success to upload, uid=" + fileUid;
            } else {
                return "Fail to upload";
            }
        } catch (Exception e) {
            return "Fail : " + e;
        }
    }

}
