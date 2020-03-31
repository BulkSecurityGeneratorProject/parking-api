package com.companyname.parking.api.infrastructure.port.adapter.rest.parkingspot;

import com.companyname.parking.api.ParkingApiServiceApplication;
import com.companyname.parking.api.application.parkingspot.ParkingSpotApplicationService;
import com.companyname.parking.api.application.parkingspot.ParkingSpotDTO;
import com.companyname.parking.api.application.parkingspot.ParkingSpotMapper;
import com.companyname.parking.api.application.parkingspot.ParkingSpotQueryService;
import com.companyname.parking.api.domain.parkingspot.ParkingSpot;
import com.companyname.parking.api.domain.user.User;
import com.companyname.parking.api.infrastructure.port.adapter.persistence.jpa.parkingspot.ParkingSpotRepository;
import com.companyname.parking.api.infrastructure.port.adapter.persistence.jpa.user.UserRepository;
import com.companyname.parking.api.infrastructure.port.adapter.rest.TestsUtil;
import com.companyname.parking.api.infrastructure.port.adapter.rest.errors.ExceptionTranslator;
import com.companyname.parking.api.infrastructure.port.adapter.rest.user.UserResourceIntTests;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

import static com.companyname.parking.api.infrastructure.port.adapter.rest.TestsUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the ParkingSpotResource REST controller.
 *
 * @see ParkingSpotResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ParkingApiServiceApplication.class)
public class ParkingSpotResourceIntTests {

    private static final String DEFAULT_NAME = "ps1";
    private static final String UPDATED_NAME = "pSpot-1";

    private static final Boolean DEFAULT_IS_FREE = false;
    private static final Boolean UPDATED_IS_FREE = true;

    @Autowired
    private ParkingSpotRepository parkingSpotRepository;

    @Autowired
    private ParkingSpotMapper parkingSpotMapper;

    @Autowired
    private ParkingSpotApplicationService parkingSpotApplicationService;

    @Autowired
    private ParkingSpotQueryService parkingSpotQueryService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    @Autowired
    private Validator validator;

    private MockMvc restParkingSpotMockMvc;

    private ParkingSpot parkingSpot;

    @Autowired
    private UserRepository userRepository;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final ParkingSpotResource parkingSpotResource = new ParkingSpotResource(parkingSpotApplicationService, parkingSpotQueryService);
        this.restParkingSpotMockMvc = MockMvcBuilders.standaloneSetup(parkingSpotResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ParkingSpot createEntity() {
        User user = UserResourceIntTests.createEntity();
        return new ParkingSpot()
            .setName(DEFAULT_NAME)
            .setFree(DEFAULT_IS_FREE)
            .setOwnedAccount(user);
    }

    /**
     * Create a randomized entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ParkingSpot createRandomizedEntity() {
        User user = UserResourceIntTests.createEntity();
        return new ParkingSpot()
            .setName(DEFAULT_NAME + RandomStringUtils.randomAlphabetic(5))
            .setFree(DEFAULT_IS_FREE)
            .setOwnedAccount(user);
    }

    @Before
    @Transactional
    public void initTest() {
        parkingSpot = createEntity();
        User persistedUser = userRepository.save(parkingSpot.getOwnedAccount());
        parkingSpot.setOwnedAccount(persistedUser);
    }

    @Test
    @Transactional
    public void createParkingSpot() throws Exception {
        int databaseSizeBeforeCreate = parkingSpotRepository.findAll().size();

        // Create the ParkingSpot
        ParkingSpotDTO parkingSpotDTO = parkingSpotMapper.toDto(parkingSpot);
        restParkingSpotMockMvc.perform(post("/parking-spots")
            .contentType(TestsUtil.APPLICATION_JSON_UTF8)
            .content(TestsUtil.convertObjectToJsonBytes(parkingSpotDTO)))
            .andExpect(status().isCreated());

        // Validate the ParkingSpot in the database
        List<ParkingSpot> parkingSpotList = parkingSpotRepository.findAll();
        assertThat(parkingSpotList).hasSize(databaseSizeBeforeCreate + 1);
        ParkingSpot testParkingSpot = parkingSpotList.get(parkingSpotList.size() - 1);
        assertThat(testParkingSpot.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testParkingSpot.isFree()).isEqualTo(DEFAULT_IS_FREE);
    }

    @Test
    @Transactional
    public void createParkingSpotWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = parkingSpotRepository.findAll().size();

        // Create the ParkingSpot with an existing ID
        parkingSpot.setId(1L);
        ParkingSpotDTO parkingSpotDTO = parkingSpotMapper.toDto(parkingSpot);

        // An entity with an existing ID cannot be created, so this API call must fail
        restParkingSpotMockMvc.perform(post("/parking-spots")
            .contentType(TestsUtil.APPLICATION_JSON_UTF8)
            .content(TestsUtil.convertObjectToJsonBytes(parkingSpotDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ParkingSpot in the database
        List<ParkingSpot> parkingSpotList = parkingSpotRepository.findAll();
        assertThat(parkingSpotList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = parkingSpotRepository.findAll().size();
        // set the field null
        parkingSpot.setName(null);

        // Create the ParkingSpot, which fails.
        ParkingSpotDTO parkingSpotDTO = parkingSpotMapper.toDto(parkingSpot);

        restParkingSpotMockMvc.perform(post("/parking-spots")
            .contentType(TestsUtil.APPLICATION_JSON_UTF8)
            .content(TestsUtil.convertObjectToJsonBytes(parkingSpotDTO)))
            .andExpect(status().isBadRequest());

        List<ParkingSpot> parkingSpotList = parkingSpotRepository.findAll();
        assertThat(parkingSpotList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkOwnedAccountIsRequired() throws Exception {
        int databaseSizeBeforeTest = parkingSpotRepository.findAll().size();
        // set the field null
        parkingSpot.setOwnedAccount(null);

        // Create the ParkingSpot, which fails.
        ParkingSpotDTO parkingSpotDTO = parkingSpotMapper.toDto(parkingSpot);

        restParkingSpotMockMvc.perform(post("/parking-spots")
            .contentType(TestsUtil.APPLICATION_JSON_UTF8)
            .content(TestsUtil.convertObjectToJsonBytes(parkingSpotDTO)))
            .andExpect(status().isBadRequest());

        List<ParkingSpot> parkingSpotList = parkingSpotRepository.findAll();
        assertThat(parkingSpotList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllParkingSpots() throws Exception {
        // Initialize the database
        parkingSpotRepository.saveAndFlush(parkingSpot);

        // Get all the parkingSpotList
        restParkingSpotMockMvc.perform(get("/parking-spots?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(parkingSpot.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].free").value(hasItem(DEFAULT_IS_FREE)))
            .andExpect(jsonPath("$.[*].ownedAccountId").value(hasItem(parkingSpot.getOwnedAccount().getId().intValue())))
            .andExpect(jsonPath("$.[*].ownedAccountLogin").value(hasItem(parkingSpot.getOwnedAccount().getLogin())));
    }
    
    @Test
    @Transactional
    public void getParkingSpot() throws Exception {
        // Initialize the database
        parkingSpotRepository.saveAndFlush(parkingSpot);

        // Get the parkingSpot
        restParkingSpotMockMvc.perform(get("/parking-spots/{id}", parkingSpot.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(parkingSpot.getId().intValue()))
                .andExpect(jsonPath("$.name").value(equalTo(DEFAULT_NAME)))
                .andExpect(jsonPath("$.free").value(equalTo(DEFAULT_IS_FREE)))
                .andExpect(jsonPath("$.ownedAccountId").value(equalTo(parkingSpot.getOwnedAccount().getId().intValue())))
                .andExpect(jsonPath("$.ownedAccountLogin").value(equalTo(parkingSpot.getOwnedAccount().getLogin())));
    }

    @Test
    @Transactional
    public void getAllParkingSpotsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        parkingSpotRepository.saveAndFlush(parkingSpot);

        // Get all the parkingSpotList where tin equals to DEFAULT_NAME
        defaultParkingSpotShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the parkingSpotList where tin equals to UPDATED_NAME
        defaultParkingSpotShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllParkingSpotsByTinIsInShouldWork() throws Exception {
        // Initialize the database
        parkingSpotRepository.saveAndFlush(parkingSpot);

        // Get all the parkingSpotList where tin in DEFAULT_NAME or UPDATED_NAME
        defaultParkingSpotShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the parkingSpotList where tin equals to UPDATED_NAME
        defaultParkingSpotShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllParkingSpotsByTinIsNullOrNotNull() throws Exception {
        // Initialize the database
        parkingSpotRepository.saveAndFlush(parkingSpot);

        // Get all the parkingSpotList where tin is not null
        defaultParkingSpotShouldBeFound("name.specified=true");

        // Get all the parkingSpotList where tin is null
        defaultParkingSpotShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    public void getAllParkingSpotsByIsFreeIsEqualToSomething() throws Exception {
        // Initialize the database
        parkingSpotRepository.saveAndFlush(parkingSpot);

        // Get all the parkingSpotList where isFree equals to DEFAULT_IS_FREE
        defaultParkingSpotShouldBeFound("isFree.equals=" + DEFAULT_IS_FREE);

        // Get all the parkingSpotList where isFree equals to UPDATED_STATUS
        defaultParkingSpotShouldNotBeFound("isFree.equals=" + UPDATED_IS_FREE);
    }

    @Test
    @Transactional
    public void getAllParkingSpotsByIsFreeIsInShouldWork() throws Exception {
        // Initialize the database
        parkingSpotRepository.saveAndFlush(parkingSpot);

        // Get all the parkingSpotList where isFree in DEFAULT_IS_FREE or UPDATED_STATUS
        defaultParkingSpotShouldBeFound("isFree.in=" + DEFAULT_IS_FREE + "," + UPDATED_IS_FREE);

        // Get all the parkingSpotList where isFree equals to UPDATED_STATUS
        defaultParkingSpotShouldNotBeFound("isFree.in=" + UPDATED_IS_FREE);
    }

    @Test
    @Transactional
    public void getAllParkingSpotsByIsFreeIsNullOrNotNull() throws Exception {
        // Initialize the database
        parkingSpotRepository.saveAndFlush(parkingSpot);

        // Get all the parkingSpotList where isFree is not null
        defaultParkingSpotShouldBeFound("isFree.specified=true");

        // Get all the parkingSpotList where isFree is null
        defaultParkingSpotShouldNotBeFound("isFree.specified=false");
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private void defaultParkingSpotShouldBeFound(String filter) throws Exception {
        restParkingSpotMockMvc.perform(get("/parking-spots?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(parkingSpot.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].free").value(hasItem(DEFAULT_IS_FREE)))
            .andExpect(jsonPath("$.[*].ownedAccountId").value(hasItem(parkingSpot.getOwnedAccount().getId().intValue())))
            .andExpect(jsonPath("$.[*].ownedAccountLogin").value(hasItem(parkingSpot.getOwnedAccount().getLogin())));

        // Check, that the count call also returns 1
        restParkingSpotMockMvc.perform(get("/parking-spots/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private void defaultParkingSpotShouldNotBeFound(String filter) throws Exception {
        restParkingSpotMockMvc.perform(get("/parking-spots?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restParkingSpotMockMvc.perform(get("/parking-spots/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("0"));
    }


    @Test
    @Transactional
    public void getNonExistingParkingSpot() throws Exception {
        // Get the parkingSpot
        restParkingSpotMockMvc.perform(get("/parking-spots/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateParkingSpot() throws Exception {
        // Initialize the database
        parkingSpotRepository.saveAndFlush(parkingSpot);

        int databaseSizeBeforeUpdate = parkingSpotRepository.findAll().size();

        // Update the parkingSpot
        ParkingSpot updatedParkingSpot = parkingSpotRepository.findById(parkingSpot.getId()).get();
        // Disconnect from session so that the updates on updatedParkingSpot are not directly saved in db
        em.detach(updatedParkingSpot);
        updatedParkingSpot
            .setName(UPDATED_NAME)
            .setFree(UPDATED_IS_FREE);
        ParkingSpotDTO parkingSpotDTO = parkingSpotMapper.toDto(updatedParkingSpot);

        restParkingSpotMockMvc.perform(put("/parking-spots")
            .contentType(TestsUtil.APPLICATION_JSON_UTF8)
            .content(TestsUtil.convertObjectToJsonBytes(parkingSpotDTO)))
            .andExpect(status().isOk());

        // Validate the ParkingSpot in the database
        List<ParkingSpot> parkingSpotList = parkingSpotRepository.findAll();
        assertThat(parkingSpotList).hasSize(databaseSizeBeforeUpdate);
        ParkingSpot testParkingSpot = parkingSpotList.get(parkingSpotList.size() - 1);
        assertThat(testParkingSpot.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testParkingSpot.isFree()).isEqualTo(UPDATED_IS_FREE);
    }

    @Test
    @Transactional
    public void updateNonExistingParkingSpot() throws Exception {
        int databaseSizeBeforeUpdate = parkingSpotRepository.findAll().size();

        // Create the ParkingSpot
        ParkingSpotDTO parkingSpotDTO = parkingSpotMapper.toDto(parkingSpot);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restParkingSpotMockMvc.perform(put("/parking-spots")
            .contentType(TestsUtil.APPLICATION_JSON_UTF8)
            .content(TestsUtil.convertObjectToJsonBytes(parkingSpotDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ParkingSpot in the database
        List<ParkingSpot> parkingSpotList = parkingSpotRepository.findAll();
        assertThat(parkingSpotList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteParkingSpot() throws Exception {
        // Initialize the database
        parkingSpotRepository.saveAndFlush(parkingSpot);

        int databaseSizeBeforeDelete = parkingSpotRepository.findAll().size();

        // Delete the parkingSpot
        restParkingSpotMockMvc.perform(delete("/parking-spots/{id}", parkingSpot.getId())
            .accept(TestsUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<ParkingSpot> parkingSpotList = parkingSpotRepository.findAll();
        assertThat(parkingSpotList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestsUtil.equalsVerifier(ParkingSpot.class);
        ParkingSpot parkingSpot1 = new ParkingSpot();
        parkingSpot1.setId(1L);
        ParkingSpot parkingSpot2 = new ParkingSpot();
        parkingSpot2.setId(parkingSpot1.getId());
        assertThat(parkingSpot1).isEqualTo(parkingSpot2);
        parkingSpot2.setId(2L);
        assertThat(parkingSpot1).isNotEqualTo(parkingSpot2);
        parkingSpot1.setId(null);
        assertThat(parkingSpot1).isNotEqualTo(parkingSpot2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestsUtil.equalsVerifier(ParkingSpotDTO.class);
        ParkingSpotDTO parkingSpotDTO1 = new ParkingSpotDTO();
        parkingSpotDTO1.setId(1L);
        ParkingSpotDTO parkingSpotDTO2 = new ParkingSpotDTO();
        assertThat(parkingSpotDTO1).isNotEqualTo(parkingSpotDTO2);
        parkingSpotDTO2.setId(parkingSpotDTO1.getId());
        assertThat(parkingSpotDTO1).isEqualTo(parkingSpotDTO2);
        parkingSpotDTO2.setId(2L);
        assertThat(parkingSpotDTO1).isNotEqualTo(parkingSpotDTO2);
        parkingSpotDTO1.setId(null);
        assertThat(parkingSpotDTO1).isNotEqualTo(parkingSpotDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(parkingSpotMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(parkingSpotMapper.fromId(null)).isNull();
    }

    @Test
    @Transactional
    public void testFreeParkingSpotsCountShouldMatch() throws Exception {
        final int targetParkingSpotCount = 100;
        List<ParkingSpot> parkingSpotList = new ArrayList<>();
        for (int i = 0; i < targetParkingSpotCount; i++)
            parkingSpotList.add(createRandomizedEntity().setFree(i % 2 == 0)); //to make list free by a half

        parkingSpotRepository.saveAll(parkingSpotList);
        parkingSpotRepository.flush();

        // Check, that the count call returns 50 percents of targetParkingSpotCount
        restParkingSpotMockMvc.perform(get("/parking-spots/count?isFree.equals=true"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(content().string(String.valueOf( targetParkingSpotCount / 2 )));

    }
}
