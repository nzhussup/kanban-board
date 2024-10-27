package com.nzhussup.kanbanservice.model.requestModels.board;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BoardAddRequest {
    private String name;
}
