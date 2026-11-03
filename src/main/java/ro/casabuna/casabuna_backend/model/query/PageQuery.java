package ro.casabuna.casabuna_backend.model.query;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;

@NoArgsConstructor
@AllArgsConstructor
public class PageQuery {

    @Min(1)
    private int page = 1;

    @Getter
    @Min(1)
    private int size = 10;

    /**
     * Returns the page number starting from 0 since hibernate pagination is 0-based
     *
     * @return the page number starting from 0
     */
    public int getPage() {
        return page - 1;
    }

    public PageRequest toPageRequest() {
        return PageRequest.of(getPage(), size);
    }

}
