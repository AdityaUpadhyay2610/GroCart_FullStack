const mysql = require('mysql2/promise');
const admin = require('firebase-admin');

// IMPORTANT: Setup your serviceAccountKey.json for Firebase admin
// const serviceAccount = require('./serviceAccountKey.json');

// admin.initializeApp({
//   credential: admin.credential.cert(serviceAccount)
// });

// Using default application credentials or a mock implementation if key not present for safety.
// Please run this script with appropriate Firebase Admin SDK credentials in production.

admin.initializeApp();
const db = admin.firestore();

async function migrate() {
    console.log("Starting Migration from MySQL to Firestore...");
    // Update credentials to match your local setup
    const connection = await mysql.createConnection({
        host: 'localhost',
        user: 'root',
        password: 'root',
        database: 'grocart_db'
    });

    try {
        const [rows, fields] = await connection.execute('SELECT * FROM internet_item');
        console.log(`Found ${rows.length} items in MySQL. Migrating...`);

        const batch = db.batch();
        
        for (const item of rows) {
            // Using ID from MySQL as the document ID for consistency
            const docRef = db.collection('items').doc(item.id.toString());
            
            // Re-mapping keys if necessary to match the strict schema
            const firestoreItem = {
                id: item.id,
                itemName: item.itemName || item.item_name || "",
                itemCategory: item.itemCategory || item.item_category || "",
                itemQuantity: item.itemQuantity || item.item_quantity || "",
                itemPrice: item.itemPrice || item.item_price || 0,
                imageUrl: item.imageUrl || item.image_url || ""
            };
            
            batch.set(docRef, firestoreItem);
            console.log(`Prepared item: ${firestoreItem.itemName}`);
        }

        await batch.commit();
        console.log("Migration to Firestore completed successfully!");

    } catch (e) {
        console.error("Migration Failed:", e);
    } finally {
        await connection.end();
    }
}

migrate();
