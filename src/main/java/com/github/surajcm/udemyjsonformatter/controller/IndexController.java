package com.github.surajcm.udemyjsonformatter.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.Part;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.nio.charset.StandardCharsets;
import java.util.Map;

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

    @Bean
    RouterFunction<ServerResponse> apiRoutes() {
        return nest(path("/api"),
                route(POST("/upload"), fileUpload()));
    }

    private HandlerFunction<ServerResponse> fileUpload() {
        return request -> request.body(BodyExtractors.toMultipartData()).flatMap(parts -> {
                    Map<String, Part> map = parts.toSingleValueMap();
                    final FilePart filePart = (FilePart) map.get("file");
                    logger.info("Uploaded file {}", filePart.filename());
                    var single = filePart.content().map(dataBuffer -> {
                        byte[] bytes = new byte[dataBuffer.readableByteCount()];
                        dataBuffer.read(bytes);
                        DataBufferUtils.release(dataBuffer);
                        return new String(bytes, StandardCharsets.UTF_8);
                    }).single();
                    return ServerResponse.ok().body(single, String.class);
                }
        );
    }
}
