package ro.casabuna.casabuna_backend.model.query;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PageQuery {

    @Min(0)
    private int page = 0;

    @Getter
    @Min(1)
    private int size = 10;

    public PageRequest toPageRequest() {
        return PageRequest.of(page, size);
    }

}
