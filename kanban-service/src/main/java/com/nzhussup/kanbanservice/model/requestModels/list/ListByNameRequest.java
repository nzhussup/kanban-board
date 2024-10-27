package com.nzhussup.kanbanservice.model.requestModels.list;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ListByNameRequest {
    private String name;
}
