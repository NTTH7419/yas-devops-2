package com.yas.webhook.model.viewmodel.webhook;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebhookListGetVm {
    List<WebhookVm> webhooks;
    int pageNo;
    int pageSize;
    long totalElements;
    long totalPages;
    boolean isLast;
}

