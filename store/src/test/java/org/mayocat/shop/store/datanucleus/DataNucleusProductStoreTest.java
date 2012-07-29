package org.mayocat.shop.store.datanucleus;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.matchers.JUnitMatchers;
import org.junit.rules.ExpectedException;
import org.mayocat.shop.model.Product;
import org.mayocat.shop.store.StoreException;

/**
 * Unit tests for the product store.
 * 
 * Note: This tests is really about datanucleus persistance, so bean-validation constraints are not tested here.
 * They are tested both in the model module directly and in full-stack REST integrations test.
 */
public class DataNucleusProductStoreTest
{
    private PersistanceManagerFactoryProdiver pmfProvider;

    private org.mayocat.shop.store.datanucleus.ProductStore ps;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp()
    {
        this.pmfProvider = new HsqldbTestingPersistanceManagerFactoryProvider();
        this.ps = new ProductStore();
        this.ps.setPersistanceManagerFactoryProdiver(pmfProvider);
    }

    @Test
    public void testPersistProduct() throws StoreException
    {
        Product p = new Product();
        p.setHandle("My-Handle");

        ps.persist("tenant", p);

        Product p2 = ps.getProduct("tenant", "My-Handle");
        Assert.assertNotNull(p2);
    }

    
    @Test
    public void testPersistProductWithSameHandleButDifferentTenant() throws StoreException
    {
        Product p = new Product();
        p.setHandle("My-Handle");
        // p.setTenant("test");

        ps.persist("tenant1", p);

        Product p2 = new Product();
        p2.setHandle("My-Handle");
        // p2.setTenant("test2");

        ps.persist("tenant2", p2);
    }

    @Test
    public void testPersistProductThatAlreadyExistsForTenant() throws StoreException
    {
        thrown.expect(StoreException.class);
        thrown.expectMessage(JUnitMatchers.containsString("unique constraint"));
        thrown.expectMessage(JUnitMatchers.containsString("UNIQUE_HANDLE_PER_TENANT"));

        Product p = new Product();
        p.setHandle("My-Handle");
        // p.setTenant("test");

        ps.persist("tenant", p);

        Product p2 = new Product();
        p2.setHandle("My-Handle");
        // p2.setTenant("test");

        ps.persist("tenant", p2);
    }
}
