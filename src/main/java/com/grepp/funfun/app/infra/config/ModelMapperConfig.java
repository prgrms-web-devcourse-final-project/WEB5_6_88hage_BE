package com.grepp.funfun.app.infra.config;

import com.grepp.funfun.app.domain.content.dto.ContentDTO;
import com.grepp.funfun.app.domain.content.entity.Content;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper(){
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setSkipNullEnabled(true);
        modelMapper.getConfiguration().setPreferNestedProperties(false);
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.LOOSE);

        Converter<Content, String> categoryConverter = ctx -> {
            if (ctx.getSource() == null ||
                    ctx.getSource().getCategory() == null ||
                    ctx.getSource().getCategory().getCategory() == null) {
                return null;
            }
            return ctx.getSource().getCategory().getCategory().name();
        };
        modelMapper.typeMap(Content.class, ContentDTO.class)
                .addMappings(mapper ->
                        mapper.using(categoryConverter)
                                .map(src -> src, ContentDTO::setCategory)
                );

        return modelMapper;

    }
    
}
