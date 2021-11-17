package com.github.surajcm.udemyjsonformatter.controller;

import com.github.surajcm.udemyjsonformatter.util.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller
public class IndexController {
    private static final Logger logger = LoggerFactory.getLogger(IndexController.class);

    @RequestMapping("/")
    public String index(final Model model) {
        return "index";
    }

    //@PostMapping("/question/uploadForEdit")
    //@ResponseStatus(value = HttpStatus.OK)
    public @ResponseBody
    String handleFileUpload(final MultipartFile upload) {
        logParams(upload);
        var responseText = "";
        try {
            responseText = new String(upload.getBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return responseText;
    }


    @PostMapping("/question/uploadForEdit")
    public @ResponseBody Mono<String> upload(@RequestPart("questionFile") Mono<FilePart> filePartMono) {
        filePartMono.doOnNext(fp -> logger.info("Received file :"+ fp.filename()));

        var abc = filePartMono.map(fp -> fp.content().map(dataBuffer -> {
            byte[] bytes = new byte[dataBuffer.readableByteCount()];
            dataBuffer.read(bytes);
            DataBufferUtils.release(dataBuffer);
            return new String(bytes, StandardCharsets.UTF_8);
        })).flatMap( g -> g.single());
        //return Mono.just("hello");
        return abc;
    }

    //@PostMapping("/question/uploadForEdit")
    public @ResponseBody Flux<String> upload(Flux<FilePart> filePartFlux) {
        return getLines(filePartFlux);
    }

    public Flux<String> getLines(Flux<FilePart> filePartFlux) {
        return filePartFlux.flatMap(filePart ->
                filePart.content().map(dataBuffer -> {
                            byte[] bytes = new byte[dataBuffer.readableByteCount()];
                            dataBuffer.read(bytes);
                            DataBufferUtils.release(dataBuffer);
                            return new String(bytes, StandardCharsets.UTF_8);
                        })
                        .map(this::processAndGetLinesAsList)
                        .flatMapIterable(Function.identity()));
    }

    private List<String> processAndGetLinesAsList(String string) {
        Supplier<Stream<String>> streamSupplier = string::lines;
        return streamSupplier.get()
                .filter(s -> !s.isBlank())
                .collect(Collectors.toList());

    }

    private void logParams(final MultipartFile upload) {
        var sanitizedOriginalFileName = CommonUtils.sanitizedString(upload.getOriginalFilename());
        var sanitizedFileSize = CommonUtils.sanitizedString(String.valueOf(upload.getSize()));
        logger.info("File details getOriginalFilename : {}, getSize : {}} ",
                sanitizedOriginalFileName, sanitizedFileSize);
    }
}
