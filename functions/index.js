/**
 * SmartWaste Kigamboni Municipal Waste Management Platform
 * Firebase Cloud Functions - High-Deliverability Email Dispatcher (Standard SMTP)
 */

require("dotenv").config();
const functions = require("firebase-functions");
const admin = require("firebase-admin");
const nodemailer = require("nodemailer");

// Initialize Firebase Admin SDK
if (!admin.apps.length) {
  admin.initializeApp();
}

const db = admin.firestore();

// ----------------------------------------------------------------------
// Configuration & Environment Variables
// ----------------------------------------------------------------------
function getEmailConfig() {
  const port = parseInt(process.env.SMTP_PORT || functions.config().smtp?.port || "587", 10);
  const rawPass = process.env.SMTP_PASS || functions.config().smtp?.pass || "";
  // Strip spaces if user copied "abcd efgh ijkl mnop" Google App Password format
  const sanitizedPass = rawPass.replace(/\s+/g, "").trim();

  return {
    // SMTP Configuration (e.g. Gmail, Outlook, Zoho, Brevo, Hostinger, Custom SMTP)
    smtpHost: process.env.SMTP_HOST || functions.config().smtp?.host || "smtp.gmail.com",
    smtpPort: port,
    // Port 465 uses SSL (secure: true). Port 587 uses STARTTLS (secure: false).
    smtpSecure: port === 465 || (process.env.SMTP_SECURE === "true" && port !== 587),
    smtpUser: (process.env.SMTP_USER || functions.config().smtp?.user || "").trim(),
    smtpPass: sanitizedPass,
    smtpFromEmail: (process.env.SMTP_FROM_EMAIL || process.env.SMTP_USER || functions.config().smtp?.from_email || "no-reply@smartwaste-kigamboni.go.tz").trim(),
    smtpFromName: (process.env.SMTP_FROM_NAME || functions.config().smtp?.from_name || "SmartWaste Kigamboni Municipal").trim(),

    appName: process.env.APP_NAME || "SmartWaste Kigamboni",
    municipalName: process.env.MUNICIPAL_COUNCIL_NAME || "Kigamboni Municipal Council",
    supportEmail: process.env.SUPPORT_EMAIL || "support@kigamboni.go.tz"
  };
}

// ----------------------------------------------------------------------
// Email Dispatch Core: Standard SMTP (Nodemailer)
// ----------------------------------------------------------------------

/**
 * Dispatches an email using Standard SMTP (Nodemailer)
 * Works seamlessly with Gmail (App Password), Outlook, Zoho, Brevo, or any custom mail server.
 */
async function sendWithSmtp(to, subject, text, html, config) {
  if (!config.smtpUser || !config.smtpPass) {
    throw new Error("SMTP credentials are not configured. Please set SMTP_USER and SMTP_PASS.");
  }

  const transporter = nodemailer.createTransport({
    host: config.smtpHost,
    port: config.smtpPort,
    secure: config.smtpSecure, // true for 465, false for other ports
    auth: {
      user: config.smtpUser,
      pass: config.smtpPass
    },
    tls: {
      rejectUnauthorized: false
    }
  });

  const mailOptions = {
    from: `"${config.smtpFromName}" <${config.smtpFromEmail || config.smtpUser}>`,
    to: Array.isArray(to) ? to.join(", ") : to,
    subject: subject,
    text: text,
    html: html
  };

  const info = await transporter.sendMail(mailOptions);
  return {
    provider: "smtp",
    statusCode: 200,
    messageId: info.messageId || "smtp-" + Date.now()
  };
}

/**
 * Dispatches email via SMTP
 */
async function dispatchEmailRobust({ to, subject, text, html }) {
  const config = getEmailConfig();

  console.log(`[SmartWaste Email] Preparing to send email via SMTP (${config.smtpHost}:${config.smtpPort}) to ${to}`);

  if (!config.smtpUser || !config.smtpPass) {
    const err = "SMTP credentials missing. Please configure SMTP_USER and SMTP_PASS environment variables (or Google App Password).";
    console.error(`[SmartWaste Email] ${err}`);
    throw new Error(err);
  }

  try {
    const res = await sendWithSmtp(to, subject, text, html, config);
    console.log(`[SmartWaste Email] SMTP delivery success to ${to}: ${JSON.stringify(res)}`);
    return res;
  } catch (err) {
    console.error(`[SmartWaste Email] SMTP delivery failed to ${to}:`, err);
    throw err;
  }
}

// ----------------------------------------------------------------------
// HTML Template Builders for High Deliverability & Mobile Responsiveness
// ----------------------------------------------------------------------

function buildOtpEmailTemplate({ name, otpCode, expiresMinutes, appName, municipalName, email }) {
  const safeName = name || "Resident";
  const safeMinutes = expiresMinutes || 10;

  const text = `Habari ${safeName}!\n\n` +
    `Your ${appName} 6-digit verification code is: ${otpCode}\n\n` +
    `This verification code will expire in ${safeMinutes} minutes.\n` +
    `Please enter this code in the mobile application to activate your account.\n\n` +
    `If you did not request this verification, please disregard this email.\n\n` +
    `${municipalName} • Environmental & Waste Management Directorate\nDar es Salaam, Tanzania`;

  const html = `<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>${appName} Verification Code</title>
  <style>
    body {
      margin: 0;
      padding: 0;
      background-color: #f1f5f9;
      font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;
      color: #1e293b;
      -webkit-font-smoothing: antialiased;
    }
    .wrapper {
      width: 100%;
      table-layout: fixed;
      background-color: #f1f5f9;
      padding: 30px 12px;
    }
    .main-card {
      max-width: 560px;
      margin: 0 auto;
      background-color: #ffffff;
      border-radius: 16px;
      overflow: hidden;
      box-shadow: 0 10px 25px rgba(0, 0, 0, 0.05);
      border: 1px solid #e2e8f0;
    }
    .header {
      background: linear-gradient(135deg, #059669 0%, #0284c7 100%);
      padding: 32px 24px;
      text-align: center;
      color: #ffffff;
    }
    .header-logo {
      font-size: 26px;
      font-weight: 800;
      letter-spacing: 0.5px;
      margin: 0;
    }
    .header-sub {
      font-size: 13px;
      margin: 6px 0 0 0;
      opacity: 0.92;
      font-weight: 500;
      letter-spacing: 0.3px;
    }
    .content {
      padding: 36px 30px;
      text-align: left;
    }
    .greeting {
      font-size: 17px;
      font-weight: 700;
      color: #0f172a;
      margin: 0 0 12px 0;
    }
    .instructions {
      font-size: 14px;
      line-height: 1.65;
      color: #475569;
      margin: 0 0 24px 0;
    }
    .otp-container {
      text-align: center;
      margin: 24px 0;
    }
    .otp-badge {
      display: inline-block;
      background-color: #f0fdf4;
      border: 2px dashed #10b981;
      border-radius: 12px;
      padding: 16px 36px;
    }
    .otp-number {
      font-family: 'Courier New', Courier, monospace;
      font-size: 38px;
      font-weight: 900;
      letter-spacing: 8px;
      color: #047857;
      margin: 0;
    }
    .timer-notice {
      text-align: center;
      font-size: 13px;
      color: #d97706;
      font-weight: 600;
      margin: 16px 0 0 0;
    }
    .security-note {
      background-color: #f8fafc;
      border-left: 4px solid #0284c7;
      padding: 12px 16px;
      border-radius: 4px;
      font-size: 12px;
      color: #64748b;
      line-height: 1.5;
      margin-top: 28px;
    }
    .footer {
      background-color: #f8fafc;
      border-top: 1px solid #e2e8f0;
      padding: 24px;
      text-align: center;
      font-size: 12px;
      color: #94a3b8;
      line-height: 1.6;
    }
  </style>
</head>
<body>
  <div class="wrapper">
    <div class="main-card">
      <div class="header">
        <h1 class="header-logo">${appName}</h1>
        <p class="header-sub">Manispaa ya Kigamboni • Municipal Waste Portal</p>
      </div>
      <div class="content">
        <p class="greeting">Habari ${safeName},</p>
        <p class="instructions">
          Thank you for registering with <strong>${appName}</strong>. Use the 6-digit verification code below to verify your email address (<strong>${email}</strong>) and complete your registration:
        </p>
        <div class="otp-container">
          <div class="otp-badge">
            <div class="otp-number">${otpCode}</div>
          </div>
        </div>
        <p class="timer-notice">⏱️ This code is valid for <strong>${safeMinutes} minutes</strong>.</p>
        
        <div class="security-note">
          🔒 <strong>Security Warning:</strong> Never share this verification code with anyone. Municipal officers will never request your code.
        </div>
      </div>
      <div class="footer">
        <strong>${municipalName}</strong><br>
        Environmental Management, Waste Collection & GIS Dispatch Directorate<br>
        Dar es Salaam, Tanzania
      </div>
    </div>
  </div>
</body>
</html>`;

  return { text, html };
}

// ----------------------------------------------------------------------
// Firestore Trigger 1: Real-time Trigger on 'otp_verifications' creation
// ----------------------------------------------------------------------
exports.onOtpVerificationCreated = functions.firestore
  .document("otp_verifications/{email}")
  .onWrite(async (change, context) => {
    // Only proceed if document was created or updated with a new code
    if (!change.after.exists) return null;

    const data = change.after.data();
    const previousData = change.before.exists ? change.before.data() : null;

    // Check if code was updated or newly created
    if (previousData && previousData.code === data.code && previousData.deliveryStatus === "SUCCESS") {
      return null; // Don't duplicate send
    }

    const email = context.params.email;
    const code = data.code;
    const expiresMinutes = 10;
    const recipientName = data.recipientName || data.name || "Resident";
    const config = getEmailConfig();

    if (!code) {
      console.warn(`[onOtpVerificationCreated] No OTP code found for ${email}`);
      return null;
    }

    console.log(`[onOtpVerificationCreated] Triggered OTP dispatch for ${email}`);

    const template = buildOtpEmailTemplate({
      name: recipientName,
      otpCode: code,
      expiresMinutes: expiresMinutes,
      appName: config.appName,
      municipalName: config.municipalName,
      email: email
    });

    try {
      const dispatchResult = await dispatchEmailRobust({
        to: email,
        subject: `Your ${config.appName} Verification Code: ${code}`,
        text: template.text,
        html: template.html
      });

      // Update Firestore document with delivery status
      await change.after.ref.update({
        deliveryStatus: "SUCCESS",
        deliveryProvider: dispatchResult.provider,
        messageId: dispatchResult.messageId,
        deliveredAt: admin.firestore.FieldValue.serverTimestamp(),
        deliveryError: null
      });

      console.log(`[onOtpVerificationCreated] Successfully delivered OTP email to ${email} via ${dispatchResult.provider}`);
      return { success: true };
    } catch (error) {
      console.error(`[onOtpVerificationCreated] Error sending OTP email to ${email}:`, error);

      await change.after.ref.update({
        deliveryStatus: "ERROR",
        deliveryError: error.message,
        lastAttemptAt: admin.firestore.FieldValue.serverTimestamp()
      });

      return { success: false, error: error.message };
    }
  });

// ----------------------------------------------------------------------
// Firestore Trigger 2: Trigger Email Queue 'mail/{mailId}'
// ----------------------------------------------------------------------
exports.onMailQueueCreated = functions.firestore
  .document("mail/{mailId}")
  .onCreate(async (snap, context) => {
    const data = snap.data();
    const mailId = context.params.mailId;

    if (!data) return null;
    if (data.delivery && data.delivery.state === "SUCCESS") return null;

    const to = data.to;
    const message = data.message || {};
    const subject = message.subject || "SmartWaste Notification";
    const text = message.text || "";
    const html = message.html || text;

    if (!to) {
      console.warn(`[onMailQueueCreated] Mail document ${mailId} missing 'to' field.`);
      return snap.ref.update({
        "delivery.state": "ERROR",
        "delivery.error": "Missing 'to' recipient email"
      });
    }

    try {
      const recipient = Array.isArray(to) ? to[0] : to;
      const result = await dispatchEmailRobust({
        to: recipient,
        subject: subject,
        text: text,
        html: html
      });

      await snap.ref.update({
        "delivery.state": "SUCCESS",
        "delivery.provider": result.provider,
        "delivery.messageId": result.messageId,
        "delivery.sentAt": admin.firestore.FieldValue.serverTimestamp()
      });

      console.log(`[onMailQueueCreated] Successfully processed mail queue ${mailId} via ${result.provider}`);
      return { success: true };
    } catch (error) {
      console.error(`[onMailQueueCreated] Failed to process mail queue ${mailId}:`, error);
      
      await snap.ref.update({
        "delivery.state": "ERROR",
        "delivery.error": error.message,
        "delivery.attempts": admin.firestore.FieldValue.increment(1),
        "delivery.lastAttempt": admin.firestore.FieldValue.serverTimestamp()
      });

      return { success: false, error: error.message };
    }
  });

// ----------------------------------------------------------------------
// Firestore Trigger 3: Driver Application Approval Status Notifications
// ----------------------------------------------------------------------
exports.onDriverApplicationStatusChanged = functions.firestore
  .document("driver_applications/{applicationId}")
  .onUpdate(async (change, context) => {
    const before = change.before.data();
    const after = change.after.data();

    // Check if status changed
    if (before.status === after.status) return null;

    const driverEmail = after.email || after.contactEmail;
    const driverName = after.fullName || "Driver Applicant";
    const status = after.status;
    const config = getEmailConfig();

    if (!driverEmail) return null;

    let subject = "";
    let messageBody = "";

    if (status === "APPROVED") {
      subject = `🎉 Driver Application Approved - ${config.appName}`;
      messageBody = `Habari ${driverName},\n\nCongratulations! Your driver application with ${config.municipalName} has been APPROVED.\nYou are now authorized to view active routes, claim municipal waste pickups, and log weighbridge disposal runs in the app.\n\nVehicle Number: ${after.vehiclePlateNumber || "N/A"}\nWard: ${after.assignedWard || "Kigamboni"}\n\nPlease sign in to the app to start accepting waste collection requests.`;
    } else if (status === "REJECTED") {
      subject = `Driver Application Update - ${config.appName}`;
      messageBody = `Habari ${driverName},\n\nYour driver application for ${config.appName} has been reviewed and was not approved at this time.\nReason / Notes: ${after.commissionerNotes || after.officerNotes || "Documentation did not meet municipal regulatory criteria"}.\n\nPlease contact the Kigamboni Municipal Directorate for further details.`;
    } else {
      return null;
    }

    const htmlBody = `<div style="font-family: Arial, sans-serif; max-width: 580px; margin: 0 auto; padding: 24px; border: 1px solid #e2e8f0; border-radius: 12px; background: #ffffff;">
      <div style="background: linear-gradient(135deg, #059669, #0284c7); padding: 20px; border-radius: 8px; color: #ffffff; text-align: center;">
        <h2 style="margin: 0;">${config.appName}</h2>
        <p style="margin: 4px 0 0 0; font-size: 13px;">${config.municipalName} • Driver Operations</p>
      </div>
      <div style="padding: 24px 0; font-size: 14px; line-height: 1.6; color: #334155;">
        <p>Habari <strong>${driverName}</strong>,</p>
        <p>${messageBody.replace(/\n/g, "<br>")}</p>
      </div>
      <div style="border-top: 1px solid #e2e8f0; padding-top: 16px; font-size: 11px; color: #94a3b8; text-align: center;">
        ${config.municipalName} • Waste Logistics Directorate
      </div>
    </div>`;

    try {
      await dispatchEmailRobust({
        to: driverEmail,
        subject: subject,
        text: messageBody,
        html: htmlBody
      });
      console.log(`[onDriverApplicationStatusChanged] Sent status update email to ${driverEmail} (${status})`);
    } catch (err) {
      console.error(`[onDriverApplicationStatusChanged] Failed to send email to ${driverEmail}:`, err);
    }

    return null;
  });

// ----------------------------------------------------------------------
// Callable Function: Directly dispatch OTP from client app
// ----------------------------------------------------------------------
exports.sendOtpEmailDirect = functions.https.onCall(async (data, context) => {
  const { email, name, code, purpose } = data;

  if (!email || !code) {
    throw new functions.https.HttpsError("invalid-argument", "Missing email or OTP code");
  }

  const config = getEmailConfig();
  const template = buildOtpEmailTemplate({
    name: name || "Resident",
    otpCode: code,
    expiresMinutes: 10,
    appName: config.appName,
    municipalName: config.municipalName,
    email: email
  });

  try {
    const result = await dispatchEmailRobust({
      to: email,
      subject: `Your ${config.appName} Verification Code: ${code}`,
      text: template.text,
      html: template.html
    });

    // Save record in Firestore
    await db.collection("otp_verifications").doc(email.trim().toLowerCase()).set({
      email: email.trim().toLowerCase(),
      code: code,
      purpose: purpose || "RESIDENT_REGISTRATION",
      expiresAt: Date.now() + 10 * 60 * 1000,
      deliveryStatus: "SUCCESS",
      deliveryProvider: result.provider,
      messageId: result.messageId,
      deliveredAt: admin.firestore.FieldValue.serverTimestamp()
    }, { merge: true });

    return { success: true, provider: result.provider, messageId: result.messageId };
  } catch (error) {
    console.error("[sendOtpEmailDirect] Dispatch failure:", error);
    throw new functions.https.HttpsError("internal", error.message);
  }
});
