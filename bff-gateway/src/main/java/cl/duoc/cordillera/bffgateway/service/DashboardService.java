package cl.duoc.cordillera.bffgateway.service;

import cl.duoc.cordillera.bffgateway.dto.DashboardResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@Service
public class DashboardService {

    private final RestTemplate restTemplate;

    @Value("${services.kpi.url}")
    private String kpiServiceUrl;

    @Value("${services.data.url}")
    private String dataServiceUrl;

    public DashboardService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public DashboardResponse getDashboard() {
        List<?> kpis = fetchKpis();
        List<?> datos = fetchDatos();
        String status = (kpis.isEmpty() && datos.isEmpty()) ? "Degradado" : "Operativo";
        List<String> alertas = Collections.emptyList();
        if (kpis.isEmpty()) {
            alertas = List.of("No fue posible obtener información desde KPI Service");
        }
        return new DashboardResponse(status, BigDecimal.ZERO, kpis, alertas);
    }

    public DashboardResponse getDashboardKpis() {
        List<?> kpis = fetchKpis();
        String status = kpis.isEmpty() ? "Degradado" : "Operativo";
        List<String> alertas = kpis.isEmpty()
                ? List.of("No fue posible obtener información desde KPI Service")
                : Collections.emptyList();
        return new DashboardResponse(status, null, kpis, alertas);
    }

    public DashboardResponse getDashboardSucursal(Long id) {
        List<?> datos = fetchDatosPorSucursal(id);
        String status = datos.isEmpty() ? "Degradado" : "Operativo";
        List<String> alertas = datos.isEmpty()
                ? List.of("No fue posible obtener información desde Data Service")
                : Collections.emptyList();
        return new DashboardResponse(status, BigDecimal.ZERO, datos, alertas);
    }

    private List<?> fetchKpis() {
        try {
            String url = kpiServiceUrl + "/api/kpis";
            ResponseEntity<List<?>> response = restTemplate.exchange(
                    url, HttpMethod.GET, null,
                    new ParameterizedTypeReference<List<?>>() {});
            return response.getBody() != null ? response.getBody() : Collections.emptyList();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    private List<?> fetchDatos() {
        try {
            String url = dataServiceUrl + "/api/datos";
            ResponseEntity<List<?>> response = restTemplate.exchange(
                    url, HttpMethod.GET, null,
                    new ParameterizedTypeReference<List<?>>() {});
            return response.getBody() != null ? response.getBody() : Collections.emptyList();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    private List<?> fetchDatosPorSucursal(Long id) {
        try {
            String url = dataServiceUrl + "/api/datos/sucursal/" + id;
            ResponseEntity<List<?>> response = restTemplate.exchange(
                    url, HttpMethod.GET, null,
                    new ParameterizedTypeReference<List<?>>() {});
            return response.getBody() != null ? response.getBody() : Collections.emptyList();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}