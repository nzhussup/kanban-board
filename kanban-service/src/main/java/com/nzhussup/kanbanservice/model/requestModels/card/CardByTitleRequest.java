package com.nzhussup.kanbanservice.model.requestModels.card;

import lombok.*;

@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CardByTitleRequest {
    private String title;
}
