db = db.getSiblingDB('uniperDB');

db.createUser({
    user: 'uniper',
    pwd: '0000',
    roles: [
        {
            role: 'readWrite',
            db: 'uniperDB'
        }
    ]
})