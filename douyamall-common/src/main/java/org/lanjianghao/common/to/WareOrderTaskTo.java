package org.lanjianghao.common.to;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WareOrderTaskTo {
    private Long taskId;
    private List<Long> detailIds;
}
