package com.nzhussup.kanbanservice.model.requestModels.list;

import lombok.*;

@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ListByNameRequest {
    private String name;
}
