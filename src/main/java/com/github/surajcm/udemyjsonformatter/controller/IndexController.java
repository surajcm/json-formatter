package com.github.surajcm.udemyjsonformatter.controller;

import com.github.surajcm.udemyjsonformatter.util.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.Part;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.springframework.web.reactive.function.BodyInserters.fromValue;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.path;
import static org.springframework.web.reactive.function.server.RouterFunctions.nest;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

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

    @Bean
    RouterFunction<ServerResponse> apiRoutes() {
        return nest(path("/api"),
                route(POST("/upload"), fileUpload()));
    }

    private HandlerFunction<ServerResponse> fileUpload() {
        return request -> request.body(BodyExtractors.toMultipartData()).flatMap(parts -> {
                    Map<String, Part> map = parts.toSingleValueMap();
                    final FilePart filePart = (FilePart) map.get("file");
                    //final String dir = "C:\\JDeveloper\\mywork\\Spring\\SpringTest\\webflux-file-upload\\uploaded";
                    //filePart.transferTo(new File(dir + "/" + filePart.filename()));
                    logger.info("Uploaded file "+filePart.filename());
                    var single = filePart.content().map(dataBuffer -> {
                        byte[] bytes = new byte[dataBuffer.readableByteCount()];
                        dataBuffer.read(bytes);
                        DataBufferUtils.release(dataBuffer);
                        String abc =  new String(bytes, StandardCharsets.UTF_8);
                        logger.info(abc);
                        return abc;
                    }).single();
                    logger.info(single.);
                    return ServerResponse.ok().body(fromValue("ok, file uploaded"));
                }
        );
    }

    public static Mono<String> readBase64Content(FilePart filePart) {
        return filePart.content().flatMap(dataBuffer -> {
            byte[] bytes = new byte[dataBuffer.readableByteCount()];
            dataBuffer.read(bytes);
            String content = Base64.getEncoder().encodeToString(bytes);
            return Mono.just(content);
        }).last();
    }


    @PostMapping("/question/uploadForEdit")
    public @ResponseBody Mono<String> upload(@RequestPart("questionFile") Mono<FilePart> filePartMono) {
        filePartMono.subscribe(fp -> logger.info("Received file :"+ fp.filename()));
        filePartMono.subscribe(
                value -> logger.info(value.filename())
                //error -> error.printStackTrace(),
                //() -> logger.info(("completed without a value")
        );
        /*var abc = filePartMono.map(fp -> fp.content().map(dataBuffer -> {
            byte[] bytes = new byte[dataBuffer.readableByteCount()];
            dataBuffer.read(bytes);
            DataBufferUtils.release(dataBuffer);
            return new String(bytes, StandardCharsets.UTF_8);
        })).flatMap( g -> g.single());*/
        return Mono.just("hello");
        //return abc;
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
