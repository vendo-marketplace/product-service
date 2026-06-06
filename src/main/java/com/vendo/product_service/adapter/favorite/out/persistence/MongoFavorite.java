package com.vendo.product_service.adapter.favorite.out.persistence;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document
@NoArgsConstructor
@AllArgsConstructor
public class MongoFavorite {

    @Id
    private String id;

    @Indexed(unique = true)
    private String userId;
    @Indexed(unique = true)
    private String productId;

}
