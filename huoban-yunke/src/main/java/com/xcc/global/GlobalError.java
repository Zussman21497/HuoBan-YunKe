/*
 * Copyright 2019-2029 geekidea(https://github.com/geekidea)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.xcc.global;

import com.xcc.commons.AjaxJson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * 全局Error/404处理
 */
@Slf4j
@RestController
public class GlobalError implements ErrorController {
    private static final String ERROR_PATH = "/error";

    @RequestMapping(ERROR_PATH)
    public AjaxJson handleError(HttpServletRequest request, HttpServletResponse response) {
        int status = response.getStatus();
//        switch (status) {
//            case HttpServletResponse.SC_UNAUTHORIZED:
//                return AjaxJson.get(status, "UNAUTHORIZED");
//            case HttpServletResponse.SC_NOT_FOUND:
//                return AjaxJson.get(status, "NOT_FOUND");
//            default:
//                break;
//        }
        return AjaxJson.get(status, "error");
    }
}
