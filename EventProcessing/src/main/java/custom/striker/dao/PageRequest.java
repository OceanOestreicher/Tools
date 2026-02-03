package custom.striker.dao;

/**
 * Represents a request to given to a data source to retrieve a page of data from
 */
public record PageRequest(int page, int pageSize) {

    /**
     * The 0 based page to fetch
     */
    @Override
    public int page() {
        return page;
    }
}
