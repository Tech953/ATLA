# Calendar sign-in — Windows 0.2.0

This desktop integration reads your default calendar. It does not send invitations or edit events. Sign-in requires your own registered OAuth application; no developer client credentials are bundled.

## Google
1. Create/select a Google Cloud project and enable Google Calendar API.
2. Configure the consent screen and add your account as a test user if the app is in testing.
3. Create an OAuth client of type Desktop app and download its JSON. Do not use a Web or Android client for this Windows flow.
4. In Engram Daily → Calendar → Import OAuth app configuration, select that JSON.
5. Select Sign in to google, authorize read-only calendar access in the browser, then return to the app. A temporary 127.0.0.1 listener receives the authorization response and closes after completion/cancellation/timeout.

## Microsoft / Outlook
1. Register an app in Microsoft Entra. Choose accounts in any organizational directory and personal Microsoft accounts if you need both types.
2. Enable public client flows for device-code authentication. Add delegated Microsoft Graph Calendars.Read permission. Your organization may require administrator approval.
3. Create a JSON file with your public application/client ID, not a client secret:

```json
{"provider":"microsoft","clientId":"YOUR-APPLICATION-CLIENT-ID"}
```

4. Import that JSON in Calendar, then select Sign in to microsoft. Enter the app's displayed code at the Microsoft sign-in page opened in your browser.

## Behavior
- Successful sign-in refreshes the default calendar. Refresh manually after changes. No automatic polling, secondary-calendar selection or provider writeback is implemented in this version; use ICS imports for other calendars.
- Snapshots cover the prior 7 days and next 90 days. Provider-expanded recurring instances are used; canceled instances are omitted. Failed refresh leaves the prior snapshot intact.
- Graph all-day occurrences with a non-UTC original timezone are retrieved in that timezone to preserve date labels.
- Tokens and imported configuration use Windows credential encryption. Tokens are not exposed to the renderer, model prompts or exports.
- Disconnect removes local tokens and cached events. Revoke the app in your provider account permissions to remove the provider-side grant too.
- One account per provider is supported. Importing new configuration replaces that provider's local connection.
- Notifications use the last retrieved snapshot while the app runs. API mode includes near-term calendar details only under the existing sharing-consent setting.
- These integrations have protocol tests with simulated providers. Real Google/Microsoft account authorization remains unverified.

Official references:
https://developers.google.com/identity/protocols/oauth2/native-app
https://developers.google.com/workspace/calendar/api/v3/reference/events/list
https://learn.microsoft.com/en-us/entra/identity-platform/v2-oauth2-device-code
https://learn.microsoft.com/en-us/graph/api/calendar-list-calendarview?view=graph-rest-1.0
