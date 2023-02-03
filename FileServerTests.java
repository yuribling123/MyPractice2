import static org.junit.Assert.*;
import org.junit.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.io.IOException;

public class FileServerTests {
	@Test 
	public void testIndex() throws URISyntaxException, IOException {
    Handler h = new Handler("./test-data");
    URI rootPath = new URI("http://localhost/");
    assertEquals("Use /search?q=... to search the files!", h.handleRequest(rootPath));
	}
	@Test 
	public void testSearch() throws URISyntaxException, IOException {
    Handler h = new Handler("./test-data/");
    URI rootPath = new URI("http://localhost/search?q=abc");
    String path1 = "./test-data/abc.txt";
    String path2 = "./test-data/abcdef.txt";
    String result = h.handleRequest(rootPath);

    assertTrue(result.startsWith("Found 2 files"));
    assertTrue(result.contains(path1));
    assertTrue(result.contains(path2));
	}
}

