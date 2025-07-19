const crypto = require('crypto');

// Simulación simple de BCrypt (para entender el problema)
// En producción real usaríamos la librería bcrypt
function simpleBCryptCheck() {
    console.log('=== Análisis de Hashes BCrypt ===\n');
    
    const hashes = {
        'testuser': '$2a$10$e0MYzXyjpJS7Pd0RVvHwHe.CxOCPtNUZRsW.jBjZplAyhz8G/L3F.',
        'admin': '$2a$10$5K8yJkGl7.H2Rs7/pVVNa.YvZQZeJZjQZ5K8yJkGl7.H2Rs7/pVVNa',
        'carolina_p': '$2a$10$wL455.T8/y.P.WJ2b2aN9uBFy1dI5a2kig7P4PDBxPj49L5L5aZ5W',
        'test_user': '$2a$10$9.d/R4/L/LqI1S.y2.zDDeuXy20o3f7V9.0d2i6C1h43j2.y5n2eW'
    };
    
    console.log('Hashes almacenados en la BD:');
    Object.entries(hashes).forEach(([user, hash]) => {
        console.log(`${user}: ${hash}`);
    });
    
    console.log('\n=== Problema Identificado ===');
    console.log('Los hashes en data.sql parecen ser de ejemplo y no corresponden a las contraseñas documentadas.');
    console.log('Necesitamos generar hashes correctos para:');
    console.log('- testuser/password123');
    console.log('- carolina_p/password123');  
    console.log('- test_user/testpass');
    console.log('- admin/admin123');
}

simpleBCryptCheck();
