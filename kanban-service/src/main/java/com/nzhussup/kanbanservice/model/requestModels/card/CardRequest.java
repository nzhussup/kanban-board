package com.nzhussup.kanbanservice.model.requestModels.card;

import lombok.*;

@Data
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CardRequest {
    private String title;
    private String description;
    private Long listId;
    private int position;
}
