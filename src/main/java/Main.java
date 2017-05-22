import static spark.Spark.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codecool.shop.controller.*;
import com.codecool.shop.dao.*;
import com.codecool.shop.dao.implementation.*;
import com.codecool.shop.model.ProductCategory;
import com.codecool.shop.model.Supplier;
import com.codecool.shop.model.Product;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.template.thymeleaf.ThymeleafTemplateEngine;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;

public class Main {

    public static String user;
    public static String psw;
    public static String database;
    public static String db_url;

    static{
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        System.setProperty("date",dateFormat.format(new Date()));
        System.setProperty("order",Integer.toString(OrderDaoMem.getInstance().getOrder().getId()));
    }

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws IOException, SQLException, ClassNotFoundException {
        logger.info("Logging started");
        readData();


        final StandardServiceRegistry registry =
                new StandardServiceRegistryBuilder()
                        .configure("hibernate.cfg.xml")
                        .build();
        SessionFactory sessionFactory = new MetadataSources(registry)
                .buildMetadata()
                .buildSessionFactory();

        Session session = sessionFactory.openSession();
        session.beginTransaction();

        // default server settings
        exception(Exception.class, (e, req, res) -> e.printStackTrace());
        staticFileLocation("/public");
        port(8888);

        // populate some data for the memory storage
        populateData(session);
        session.getTransaction().commit();

        // Always start with more specific routes
        get("/cart", (Request req, Response res) -> {
            return new ThymeleafTemplateEngine().render(ProductController.renderCart(req, res));
        });

        get("/login", (Request req, Response res) -> {
            return new ThymeleafTemplateEngine().render(ProductController.login(req, res));
        });

        get("/register", (Request req, Response res) -> {
            return new ThymeleafTemplateEngine().render(ProductController.register(req, res));
        });

        post("/cart/edit/:id", (Request req, Response res) -> {
            return new ThymeleafTemplateEngine().render(ProductController.editItem(req, res) );

        });

        get("/cart/delete/:id", (Request req, Response res) -> {
            return new ThymeleafTemplateEngine().render(ProductController.deleteItem(req, res));
        });

        post("/register_user", (Request req, Response res) -> {
            return new ThymeleafTemplateEngine().render(ProductController.register_user(req, res,session) );
        });

        post("/login_user", (Request req, Response res) -> {
            return new ThymeleafTemplateEngine().render(ProductController.login_user(req, res, session) );
        });



        // Always add generic routes to the end
        get("/", ProductController::renderProducts, new ThymeleafTemplateEngine());
        // Equivalent with above
        get("/index", (Request req, Response res) -> {
            ModelAndView modelAndView=ProductController.renderProducts(req, res);
            //setProperties(req);
            return new ThymeleafTemplateEngine().render(modelAndView);
        });

        get ("/:id", (req, res) -> {
            res.redirect("/");
            OrderController.addProductToOrder(req, res);
            return null;
        });


        get("/categories/:id", ProductController::renderProductsByCategory, new ThymeleafTemplateEngine());

        get("/suppliers/:id", ProductController::renderProductsBySupplier, new ThymeleafTemplateEngine());

        get("/order/checkout", CheckoutController::renderCheckoutForm, new ThymeleafTemplateEngine());

        get("/order/payment", PaymentController::renderPayment, new ThymeleafTemplateEngine());

        post("/order/checkout/done", (request, response) -> {

            return new ThymeleafTemplateEngine().render( OrderController.addPerson(request, response, session) );
        });

        post("/order/payment/done", (request, response) -> {

            return new ThymeleafTemplateEngine().render( OrderController.setPaidStatus(request, response) );
        });

    }

    public static void populateData(Session session) {

        ProductDao productDataStore = ProductDaoMem.getInstance();
        ProductCategoryDao productCategoryDataStore = ProductCategoryDaoMem.getInstance();
        SupplierDao supplierDataStore = SupplierDaoMem.getInstance();
        OrderDaoMem orderDaoMem = OrderDaoMem.getInstance();

        //setting up a new supplier
        Supplier amazon = new Supplier("Amazon", "Digital content and services");
        session.save(amazon);
        supplierDataStore.add(amazon);
        Supplier lenovo = new Supplier("Lenovo", "Computers");
        supplierDataStore.add(lenovo);
        session.save(lenovo);

        //setting up a new product category
        ProductCategory tablet = new ProductCategory("Tablet", "Hardware", "A tablet computer, commonly shortened to tablet, is a thin, flat mobile computer with a touchscreen display.");
        productCategoryDataStore.add(tablet);
        session.save(tablet);

        //setting up products and printing it
        Product prod=new Product("Amazon Fire", 49.9f, "USD", "Fantastic price. Large content ecosystem. Good parental controls. Helpful technical support.", tablet, amazon);
        session.save(prod);
        Product prod_new=new Product("Lenovo IdeaPad Miix 700", 479, "USD", "Keyboard cover is included. Fanless Core m5 processor. Full-size USB ports. Adjustable kickstand.", tablet, lenovo);
        session.save(prod_new);
        Product prod_old=new Product("Amazon Fire HD 8", 89, "USD", "Amazon's latest Fire HD 8 tablet is a great value for media consumption.", tablet, amazon);
        session.save(prod_old);

        productDataStore.add(new Product("Amazon Fire", 49.9f, "USD", "Fantastic price. Large content ecosystem. Good parental controls. Helpful technical support.", tablet, amazon));
        productDataStore.add(new Product("Lenovo IdeaPad Miix 700", 479, "USD", "Keyboard cover is included. Fanless Core m5 processor. Full-size USB ports. Adjustable kickstand.", tablet, lenovo));
        productDataStore.add(new Product("Amazon Fire HD 8", 89, "USD", "Amazon's latest Fire HD 8 tablet is a great value for media consumption.", tablet, amazon));

    }

    public static void readData(){
        try {
            Properties properties = new Properties();
            InputStream inputStream = Main.class.getResourceAsStream("sql/connection.properties");
            properties.load(inputStream);
            database=properties.getProperty("database");
            user=properties.getProperty("user");
            db_url=properties.getProperty("url");
            psw=properties.getProperty("password");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
