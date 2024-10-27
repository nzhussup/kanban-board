package com.nzhussup.kanbanservice.model.requestModels.board;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BoardAddAdminRequest {
    private String name;
    private Long ownerId;
}
