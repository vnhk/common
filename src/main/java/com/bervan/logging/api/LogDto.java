package com.bervan.logging.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LogDto {
    private Long id;
    private String applicationName;
    private String logLevel;
    private String className;
    private String methodName;
    private String processName;
    private String moduleName;
    private String packageName;
    private String route;
    private LocalDateTime timestamp;
    private String message;
    private String fullLog;
    private int lineNumber;
}
