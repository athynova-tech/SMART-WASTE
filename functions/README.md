# SmartWaste Kigamboni - Firebase Cloud Functions (SMTP Email Delivery)

This module handles real-time email dispatching for SmartWaste Kigamboni using **Standard SMTP (Nodemailer)**.

## ✨ Standard SMTP Support
Works seamlessly with **Gmail (Google App Password)**, Outlook/Office 365, Zoho Mail, Brevo, Hostinger, cPanel, or any custom mail server.

---

## 🔑 How to Set Up Gmail SMTP (Recommended & Free)

1. Go to your Google Account at **https://myaccount.google.com/security**.
2. Ensure **2-Step Verification** is turned **ON**.
3. Go to **App Passwords** at **https://myaccount.google.com/apppasswords**.
4. Type **SmartWaste** in the app name field and click **Create**.
5. Copy the generated **16-character password** (e.g. `abcd efgh ijkl mnop`).
6. Set in your `functions/.env` file:
   ```bash
   SMTP_HOST=smtp.gmail.com
   SMTP_PORT=465
   SMTP_SECURE=true
   SMTP_USER=your_email@gmail.com
   SMTP_PASS=abcdefghijklmnop
   SMTP_FROM_EMAIL=your_email@gmail.com
   ```

---

## 🚀 Deployment Instructions

### 1. Install Dependencies
```bash
cd functions
npm install
```

### 2. Configure Firebase Environment Config (for Cloud Functions)
```bash
firebase functions:config:set smtp.user="your_email@gmail.com" smtp.pass="your_16_character_app_password" smtp.host="smtp.gmail.com" smtp.port="465"
```

### 3. Deploy Functions
```bash
firebase deploy --only functions
```

