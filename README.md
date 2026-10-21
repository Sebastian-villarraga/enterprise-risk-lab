# Enterprise Risk API

Small risk-control system: a React SPA authenticates a person with Auth0 and calls a Spring Boot OAuth 2.0 resource server using a bearer access token. The API only accepts JWTs for the configured issuer and audience.

**Deployment URL:** https://enterprise-risk-sebastian.duckdns.org

## Local setup

1. In Auth0 create an API with audience `https://enterprise-risk-api`, algorithm RS256, and permission `read:risk`. Create an SPA and grant it **User-Delegated Access** to that API.
2. Copy `frontend/.env.example` to `frontend/.env.local` and provide the Auth0 domain and SPA client ID. This file is ignored by Git.
3. Start the backend:

   ```powershell
   mvn spring-boot:run
   ```

4. In another terminal start the frontend:

   ```powershell
   cd frontend
   npm install
   npm run dev
   ```

Open the address reported by Vite (normally `http://localhost:5173`), log in, then select **Call protected API**.

## Checks

- `GET http://localhost:8080/api/risk-status` without a bearer token returns `401`.
- `GET http://localhost:8080/actuator/health` is public and returns the service health.
- The application test suite checks both the unauthenticated `401` and the successful authenticated endpoint flow:

  ```powershell
  mvn test
  ```

## Deployment

Build the artifacts with `mvn clean package` and `npm run build` from `frontend/`. The ready-to-customize server templates are in `deployment/`:

- `enterprise-risk-api.service` runs the JAR as the dedicated `riskapi` user.
- `enterprise-risk.conf` serves the SPA and proxies only `/api/` to `127.0.0.1:8080`.

Configure Auth0 callback, logout and web origins with `https://enterprise-risk-sebastian.duckdns.org`. Enable Apache modules `ssl proxy proxy_http rewrite headers`, then obtain and verify the certificate:

```bash
sudo certbot --apache -d YOUR-SUBDOMAIN.duckdns.org
sudo certbot renew --dry-run
```

Do not expose port 8080 publicly. Allow only ports 80 and 443 at the host or cloud firewall.

## Evidence to submit after deployment

Capture (1) the authenticated API result in the browser and (2) the browser certificate details or successful `certbot renew --dry-run` output. These cannot be produced until an Auth0 tenant and a public Duck DNS host are supplied.
