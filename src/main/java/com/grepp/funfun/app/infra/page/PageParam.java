package com.grepp.funfun.app.infra.page;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class PageParam{
    @Min(1)
    private int page = 1;
    @Min(1)
    private int size = 5;
}
