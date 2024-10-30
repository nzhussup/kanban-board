package com.nzhussup.kanbanservice.model.requestModels.list;

import lombok.*;

@Data
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListRequest {

    private String name;
    private long boardId;
    private int position;

}
