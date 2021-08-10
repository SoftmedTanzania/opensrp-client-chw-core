package org.smartregister.chw.core.utils;

import org.hamcrest.MatcherAssert;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.smartregister.domain.Alert;
import org.smartregister.domain.AlertStatus;
import org.smartregister.immunization.ImmunizationLibrary;
import org.smartregister.immunization.db.VaccineRepo;
import org.smartregister.immunization.domain.Vaccine;
import org.smartregister.immunization.domain.VaccineSchedule;
import org.smartregister.immunization.domain.VaccineWrapper;
import org.smartregister.immunization.util.VaccineCache;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ImmunizationLibrary.class)
public class VisitVaccineUtilTest {
    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Before
    public void setUp(){
        mockStatic(ImmunizationLibrary.class);
    }

    @Test
    public void testGetAllVaccinesGetsAllWomanAndChild() {
        Map<String, VaccineCache> vaccineMapMock = mock(HashMap.class);
        VaccineRepo.Vaccine womanVaccine = VaccineRepo.Vaccine.rubella1;
        VaccineCache womanVaccineCache = new VaccineCache();
        womanVaccineCache.vaccineRepo = Collections.singletonList(womanVaccine);

        VaccineRepo.Vaccine childVaccine = VaccineRepo.Vaccine.measles1;
        VaccineCache childVaccineCache = new VaccineCache();
        childVaccineCache.vaccineRepo = Collections.singletonList(childVaccine);

        when(vaccineMapMock.get(eq("woman"))).thenReturn(womanVaccineCache);
        when(vaccineMapMock.get(eq("child"))).thenReturn(childVaccineCache);

        when(ImmunizationLibrary.getVaccineCacheMap())
                .thenReturn(vaccineMapMock);

        Map<String, VaccineRepo.Vaccine> vaccinesMap = VisitVaccineUtil.getAllVaccines();
        String womanVaccineKey = womanVaccine.display().toLowerCase().replace(" ", "").replace("-", "");
        String childVaccineKey = childVaccine.display().toLowerCase().replace(" ", "").replace("-", "");
        Assert.assertEquals(2, vaccinesMap.size());
        Assert.assertEquals(womanVaccine, vaccinesMap.get(womanVaccineKey));
        Assert.assertEquals(childVaccine, vaccinesMap.get(childVaccineKey));
    }

    @Test
    public void testWrapVaccine(){
        VaccineRepo.Vaccine vaccine = VaccineRepo.Vaccine.bcg;
        Alert alert = mock(Alert.class);
        VaccineWrapper wrapped = VisitVaccineUtil.wrapVaccine(vaccine, alert);
        Assert.assertEquals(vaccine, wrapped.getVaccine());
        Assert.assertEquals(vaccine.display(), wrapped.getName());
        Assert.assertEquals(vaccine.display(), wrapped.getDefaultName());
        Assert.assertEquals(alert, wrapped.getAlert());
    }

    @Test
    public void testGroupByTypeReturnsGrouped(){
        String scheduleName1 = "Ante Natal Care - Normal";
        String scheduleName2 = "TT";
        Alert scheduleName1Alert1 = new Alert("Case X", scheduleName1, "ANC 1", AlertStatus.normal, "2012-01-01", "2012-01-11");
        Alert scheduleName1Alert2 = new Alert("Case Y", scheduleName1, "ANC 2", AlertStatus.complete, "2012-01-01", "2012-01-11");
        Alert scheduleName2Alert = new Alert("Case X", scheduleName2, "TT 1", AlertStatus.normal, "2012-01-01", "2012-01-11");
        List<Alert> alerts  = Arrays.asList(scheduleName1Alert1, scheduleName1Alert2, scheduleName2Alert);

        Map<String, List<Alert>> grouped = VisitVaccineUtil.groupByType(alerts);

        String type1Key = scheduleName1.substring(0, scheduleName1.length() - 1).trim();
        String type2Key = scheduleName2.substring(0, scheduleName2.length() - 1).trim();
        Assert.assertEquals(2, grouped.get(type1Key).size());
        MatcherAssert.assertThat(grouped.get(type1Key), hasItems(scheduleName1Alert1, scheduleName1Alert2));
        Assert.assertEquals(1, grouped.get(type2Key).size());
        MatcherAssert.assertThat(grouped.get(type2Key), hasItem(scheduleName2Alert));
    }

    @Test
    public void testGetAlertIterationReturnsScheduleNameSubstring(){
        Alert bcgAlert = new Alert("child id 1", "BCG", "bcg", AlertStatus.normal, "2013-01-01", "2013-02-01");
        String iteration = VisitVaccineUtil.getAlertIteration(bcgAlert);
        Assert.assertEquals(bcgAlert.scheduleName().substring(bcgAlert.scheduleName().length() - 1), iteration);
    }

    @Test
    public void testGetInMemoryAlertsGetsOfflineAlerts(){
        Alert offlineAlert = new Alert("entity id 1", "ANC", "ANC 1", AlertStatus.normal, "2013-01-01", "2013-02-01", true);
        VaccineSchedule vaccineSchedule = mock(VaccineSchedule.class);
        when(vaccineSchedule.getOfflineAlert(anyString(), any(Date.class), anyList()))
                .thenReturn(offlineAlert);
        HashMap<String, VaccineSchedule> tmpMap = new HashMap<>();
        tmpMap.put("inner", vaccineSchedule);
        String vaccineCategory = "outer";
        HashMap<String, HashMap<String, VaccineSchedule>> vaccineSchedules = new HashMap<>();
        vaccineSchedules.put(vaccineCategory, tmpMap);
        String baseEntityId = "5e90a36d-1b33-4fbd-a357-2f9410df44c6";
        DateTime dob = DateTime.now();
        List<Vaccine> issuedVaccines = new ArrayList<>();

        List<Alert> inMemoryAlerts = VisitVaccineUtil.getInMemoryAlerts(vaccineSchedules, baseEntityId, dob, vaccineCategory, issuedVaccines);
        verify(vaccineSchedule, atLeastOnce()).getOfflineAlert(eq(baseEntityId), eq(dob.toDate()), eq(issuedVaccines));
        Assert.assertEquals(1, inMemoryAlerts.size());
        MatcherAssert.assertThat(inMemoryAlerts, hasItem(offlineAlert));
    }
}
