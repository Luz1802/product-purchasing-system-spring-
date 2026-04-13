package co.edu.cesde.pps;

import co.edu.cesde.pps.dto.ProductDTO;
import co.edu.cesde.pps.model.Product;
import co.edu.cesde.pps.repository.ProductRepository;
import co.edu.cesde.pps.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("mysql-test")
@Transactional(readOnly = true)
@EnabledIfEnvironmentVariable(named = "RUN_MYSQL_TESTS", matches = "(?i)true")
class ProductMySqlConnectivityTest {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductService productService;

    @Test
    void shouldLoadSpringContextConnectToMySqlAndQueryProducts() throws Exception {
        assertThat(dataSource).isNotNull();
        assertThat(productRepository).isNotNull();
        assertThat(productService).isNotNull();

        try (Connection connection = dataSource.getConnection()) {
            assertThat(connection).isNotNull();
            assertThat(connection.isClosed()).isFalse();
            String databaseProductName = connection.getMetaData().getDatabaseProductName().toLowerCase();
            assertThat(databaseProductName).containsAnyOf("mysql", "mariadb");

            try (ResultSet tables = connection.getMetaData().getTables(connection.getCatalog(), null, "products", null)) {
                assertThat(tables.next()).isTrue();
            }
        }

        long totalProducts = productRepository.count();
        assertThat(totalProducts).isGreaterThan(0L);

        List<Product> repositoryProducts = productRepository.findAll();
        assertThat(repositoryProducts)
                .isNotEmpty()
                .hasSize((int) totalProducts);

        Product firstProduct = repositoryProducts.get(0);
        assertThat(firstProduct.getProductId()).isNotNull();
        assertThat(firstProduct.getSku()).isNotBlank();
        assertThat(firstProduct.getName()).isNotBlank();
        assertThat(firstProduct.getPrice()).isNotNull();
        assertThat(firstProduct.getStockQty()).isNotNull();
        assertThat(firstProduct.getCategory()).isNotNull();
        assertThat(firstProduct.getCategory().getCategoryId()).isNotNull();

        ProductDTO productFromService = productService.findBySku(firstProduct.getSku());
        assertThat(productFromService).isNotNull();
        assertThat(productFromService.getProductId()).isEqualTo(firstProduct.getProductId());
        assertThat(productFromService.getSku()).isEqualTo(firstProduct.getSku());
        assertThat(productFromService.getName()).isEqualTo(firstProduct.getName());
        assertThat(productFromService.getPrice()).isEqualByComparingTo(firstProduct.getPrice());
        assertThat(productFromService.getCategoryId()).isEqualTo(firstProduct.getCategory().getCategoryId());
        assertThat(productFromService.getCategoryName()).isNotBlank();

        List<ProductDTO> productsFromService = productService.findAllProducts();
        assertThat(productsFromService)
                .isNotEmpty()
                .hasSize(repositoryProducts.size());

        List<Product> activeRepositoryProducts = productRepository.findByIsActiveTrue();
        List<ProductDTO> activeProductsFromService = productService.findActiveProducts();
        assertThat(activeProductsFromService).hasSize(activeRepositoryProducts.size());

        String searchToken = firstProduct.getName().substring(0, Math.min(3, firstProduct.getName().length()));
        List<ProductDTO> searchResults = productService.searchByName(searchToken);
        assertThat(searchResults).isNotEmpty();
        assertThat(searchResults)
                .extracting(ProductDTO::getProductId)
                .contains(firstProduct.getProductId());

        List<ProductDTO> productsByCategory = productService.findByCategory(firstProduct.getCategory().getCategoryId());
        assertThat(productsByCategory).isNotEmpty();
        assertThat(productsByCategory)
                .extracting(ProductDTO::getCategoryId)
                .containsOnly(firstProduct.getCategory().getCategoryId());
    }
}

