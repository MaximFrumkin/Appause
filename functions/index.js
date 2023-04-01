const functions = require("firebase-functions");
const admin = require("firebase-admin");
admin.initializeApp();

// listener method
exports.sendListenerPushNotification =
    functions.database.ref("/users/{userId}/milestone")
        .onWrite((data, context) => {
          const userId = context.params.userId;
          // Now we fetch the fcmtoken of all their friends
          const FCMToken = admin.database()
              .ref(`/FCMTokens/${userId}`).once("value");
          const message = "SERGIU!?";
          const payload = {
            token: FCMToken,
            notification: {
              title: "cloud function demo",
              body: message,
            },
            data: {
              body: message,
            },
          };

          admin.messaging().send(payload).then((response) => {
            // Response is a message ID string.
            console.log("Successfully sent message:", response);
            return {success: true};
          }).catch((error) => {
            return {error: error.code};
          });
        });
