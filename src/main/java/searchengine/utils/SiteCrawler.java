package searchengine.utils;



import java.util.concurrent.RecursiveTask;

public class SiteCrawler extends RecursiveTask<Void>
{
    private String url;

    public SiteCrawler(String url) {
        this.url = url;
    }

    @Override
    protected Void compute() {
        return null;
    }
}
