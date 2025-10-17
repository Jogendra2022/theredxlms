    const functions = require("firebase-functions");
    const admin = require("firebase-admin");

    admin.initializeApp();

    exports.sendNotificationToTopic = functions.https.onCall(async (data, context) => {
      // This checks if the person calling the function is logged in.
      // It's a good security measure for your admin panel.
      if (!context.auth) {
        throw new functions.https.HttpsError(
          "unauthenticated",
          "The function must be called while authenticated."
        );
      }

      const title = data.title;
      const body = data.body;
      const topic = "all_students"; // The topic all your student apps are subscribed to.

      // Check that the app actually sent a title and body.
      if (!title || !body) {
        throw new functions.https.HttpsError(
          "invalid-argument",
          "The function must be called with 'title' and 'body' arguments."
        );
      }

      // This is the actual notification content.
      const payload = {
        notification: {
          title: title,
          body: body,
        },
        topic: topic, // Tell FCM to send it to everyone subscribed to this topic.
      };

      try {
        // Send the notification using the Firebase Admin SDK.
        const response = await admin.messaging().send(payload);
        console.log("Successfully sent message:", response);
        return { success: true, messageId: response };
      } catch (error) {
        console.error("Error sending message:", error);
        throw new functions.https.HttpsError(
          "internal",
          "An error occurred while trying to send the notification."
        );
      }
    });
    