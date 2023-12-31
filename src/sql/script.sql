USE [master]
GO
/****** Object:  Database [siman]    Script Date: 13/10/23 5:51:40 PM ******/
CREATE DATABASE [siman]
 CONTAINMENT = NONE
 ON  PRIMARY 
( NAME = N'siman', FILENAME = N'C:\Program Files\Microsoft SQL Server\MSSQL15.CARLOS\MSSQL\DATA\siman.mdf' , SIZE = 8192KB , MAXSIZE = UNLIMITED, FILEGROWTH = 65536KB )
 LOG ON 
( NAME = N'siman_log', FILENAME = N'C:\Program Files\Microsoft SQL Server\MSSQL15.CARLOS\MSSQL\DATA\siman_log.ldf' , SIZE = 8192KB , MAXSIZE = 2048GB , FILEGROWTH = 65536KB )
 WITH CATALOG_COLLATION = DATABASE_DEFAULT
GO
ALTER DATABASE [siman] SET COMPATIBILITY_LEVEL = 150
GO
IF (1 = FULLTEXTSERVICEPROPERTY('IsFullTextInstalled'))
begin
EXEC [siman].[dbo].[sp_fulltext_database] @action = 'enable'
end
GO
ALTER DATABASE [siman] SET ANSI_NULL_DEFAULT OFF 
GO
ALTER DATABASE [siman] SET ANSI_NULLS OFF 
GO
ALTER DATABASE [siman] SET ANSI_PADDING OFF 
GO
ALTER DATABASE [siman] SET ANSI_WARNINGS OFF 
GO
ALTER DATABASE [siman] SET ARITHABORT OFF 
GO
ALTER DATABASE [siman] SET AUTO_CLOSE ON 
GO
ALTER DATABASE [siman] SET AUTO_SHRINK OFF 
GO
ALTER DATABASE [siman] SET AUTO_UPDATE_STATISTICS ON 
GO
ALTER DATABASE [siman] SET CURSOR_CLOSE_ON_COMMIT OFF 
GO
ALTER DATABASE [siman] SET CURSOR_DEFAULT  GLOBAL 
GO
ALTER DATABASE [siman] SET CONCAT_NULL_YIELDS_NULL OFF 
GO
ALTER DATABASE [siman] SET NUMERIC_ROUNDABORT OFF 
GO
ALTER DATABASE [siman] SET QUOTED_IDENTIFIER OFF 
GO
ALTER DATABASE [siman] SET RECURSIVE_TRIGGERS OFF 
GO
ALTER DATABASE [siman] SET  ENABLE_BROKER 
GO
ALTER DATABASE [siman] SET AUTO_UPDATE_STATISTICS_ASYNC OFF 
GO
ALTER DATABASE [siman] SET DATE_CORRELATION_OPTIMIZATION OFF 
GO
ALTER DATABASE [siman] SET TRUSTWORTHY OFF 
GO
ALTER DATABASE [siman] SET ALLOW_SNAPSHOT_ISOLATION OFF 
GO
ALTER DATABASE [siman] SET PARAMETERIZATION SIMPLE 
GO
ALTER DATABASE [siman] SET READ_COMMITTED_SNAPSHOT OFF 
GO
ALTER DATABASE [siman] SET HONOR_BROKER_PRIORITY OFF 
GO
ALTER DATABASE [siman] SET RECOVERY SIMPLE 
GO
ALTER DATABASE [siman] SET  MULTI_USER 
GO
ALTER DATABASE [siman] SET PAGE_VERIFY CHECKSUM  
GO
ALTER DATABASE [siman] SET DB_CHAINING OFF 
GO
ALTER DATABASE [siman] SET FILESTREAM( NON_TRANSACTED_ACCESS = OFF ) 
GO
ALTER DATABASE [siman] SET TARGET_RECOVERY_TIME = 60 SECONDS 
GO
ALTER DATABASE [siman] SET DELAYED_DURABILITY = DISABLED 
GO
ALTER DATABASE [siman] SET ACCELERATED_DATABASE_RECOVERY = OFF  
GO
ALTER DATABASE [siman] SET QUERY_STORE = OFF
GO
USE [siman]
GO
/****** Object:  User [carlos]    Script Date: 13/10/23 5:51:40 PM ******/
CREATE USER [carlos] FOR LOGIN [carlos] WITH DEFAULT_SCHEMA=[dbo]
GO
/****** Object:  User [admin]    Script Date: 13/10/23 5:51:40 PM ******/
CREATE USER [admin] FOR LOGIN [admin] WITH DEFAULT_SCHEMA=[dbo]
GO
ALTER ROLE [db_owner] ADD MEMBER [carlos]
GO
ALTER ROLE [db_owner] ADD MEMBER [admin]
GO
/****** Object:  Table [dbo].[asignaciones]    Script Date: 13/10/23 5:51:40 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[asignaciones](
	[id_asignacion] [int] IDENTITY(0,1) NOT NULL,
	[id_colaborador] [int] NOT NULL,
	[id_sucursal] [int] NOT NULL,
	[distancia] [decimal](4, 2) NOT NULL,
 CONSTRAINT [asignaciones_PK] PRIMARY KEY CLUSTERED 
(
	[id_asignacion] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[ciudades]    Script Date: 13/10/23 5:51:40 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[ciudades](
	[id_ciudad] [int] IDENTITY(0,1) NOT NULL,
	[nombre_ciudad] [varchar](100) NOT NULL,
 CONSTRAINT [ciudades_PK] PRIMARY KEY CLUSTERED 
(
	[id_ciudad] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[colaboradores]    Script Date: 13/10/23 5:51:40 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[colaboradores](
	[id_colaborador] [int] IDENTITY(0,1) NOT NULL,
	[nombre_colaborador] [varchar](50) NOT NULL,
	[apellido_colaborador] [varchar](50) NOT NULL,
	[numero_identidad_colaborador] [varchar](13) NOT NULL,
	[id_puesto] [int] NOT NULL,
 CONSTRAINT [colaboradores_PK] PRIMARY KEY CLUSTERED 
(
	[id_colaborador] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
 CONSTRAINT [colaboradores_UN] UNIQUE NONCLUSTERED 
(
	[numero_identidad_colaborador] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[puestos]    Script Date: 13/10/23 5:51:40 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[puestos](
	[id_puesto] [int] IDENTITY(0,1) NOT NULL,
	[puesto] [varchar](100) NOT NULL,
 CONSTRAINT [puestos_PK] PRIMARY KEY CLUSTERED 
(
	[id_puesto] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
 CONSTRAINT [puestos_UN] UNIQUE NONCLUSTERED 
(
	[puesto] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[sucursales]    Script Date: 13/10/23 5:51:40 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[sucursales](
	[id_sucursal] [int] IDENTITY(0,1) NOT NULL,
	[nombre_sucursal] [varchar](100) NOT NULL,
	[direccion_sucursal] [varchar](200) NOT NULL,
	[id_ciudad] [int] NOT NULL,
 CONSTRAINT [sucursales_PK] PRIMARY KEY CLUSTERED 
(
	[id_sucursal] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
 CONSTRAINT [sucursales_UN] UNIQUE NONCLUSTERED 
(
	[nombre_sucursal] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[tarifa]    Script Date: 13/10/23 5:51:40 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[tarifa](
	[id_tarifa] [int] IDENTITY(0,1) NOT NULL,
	[tarifa] [money] NOT NULL,
 CONSTRAINT [tarifa_PK] PRIMARY KEY CLUSTERED 
(
	[id_tarifa] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[transportistas]    Script Date: 13/10/23 5:51:40 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[transportistas](
	[id_transportista] [int] IDENTITY(0,1) NOT NULL,
	[nombre_transportista] [varchar](100) NOT NULL,
	[id_tarifa] [int] NOT NULL,
	[numero_identidad_transportista] [varchar](13) NOT NULL,
 CONSTRAINT [transportistas_PK] PRIMARY KEY CLUSTERED 
(
	[id_transportista] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
 CONSTRAINT [transportistas_UN] UNIQUE NONCLUSTERED 
(
	[numero_identidad_transportista] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[usuarios]    Script Date: 13/10/23 5:51:40 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[usuarios](
	[nombre_usuario] [varchar](50) NOT NULL,
	[password_usuario] [varchar](40) NOT NULL,
	[id_colaborador] [int] NOT NULL,
 CONSTRAINT [usuarios_PK] PRIMARY KEY CLUSTERED 
(
	[nombre_usuario] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[viajes_detalle]    Script Date: 13/10/23 5:51:40 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[viajes_detalle](
	[id_viaje_detalle] [int] IDENTITY(0,1) NOT NULL,
	[id_viaje_encabezado] [int] NOT NULL,
	[id_colaborador] [int] NOT NULL,
	[id_asignacion] [int] NULL,
 CONSTRAINT [viajes_detalle_PK] PRIMARY KEY CLUSTERED 
(
	[id_viaje_detalle] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[viajes_encabezado]    Script Date: 13/10/23 5:51:40 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[viajes_encabezado](
	[id_viaje_encabezado] [int] IDENTITY(0,1) NOT NULL,
	[id_colaborador] [int] NOT NULL,
	[id_transportista] [int] NOT NULL,
	[fecha_viaje] [date] NOT NULL,
	[total_viaje] [money] NOT NULL,
 CONSTRAINT [viajes_encabezado_PK] PRIMARY KEY CLUSTERED 
(
	[id_viaje_encabezado] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
ALTER TABLE [dbo].[asignaciones]  WITH CHECK ADD  CONSTRAINT [asignaciones_FK] FOREIGN KEY([id_colaborador])
REFERENCES [dbo].[colaboradores] ([id_colaborador])
GO
ALTER TABLE [dbo].[asignaciones] CHECK CONSTRAINT [asignaciones_FK]
GO
ALTER TABLE [dbo].[asignaciones]  WITH CHECK ADD  CONSTRAINT [asignaciones_FK_1] FOREIGN KEY([id_sucursal])
REFERENCES [dbo].[sucursales] ([id_sucursal])
GO
ALTER TABLE [dbo].[asignaciones] CHECK CONSTRAINT [asignaciones_FK_1]
GO
ALTER TABLE [dbo].[colaboradores]  WITH CHECK ADD  CONSTRAINT [colaboradores_FK_1] FOREIGN KEY([id_puesto])
REFERENCES [dbo].[puestos] ([id_puesto])
GO
ALTER TABLE [dbo].[colaboradores] CHECK CONSTRAINT [colaboradores_FK_1]
GO
ALTER TABLE [dbo].[sucursales]  WITH CHECK ADD  CONSTRAINT [sucursales_FK] FOREIGN KEY([id_ciudad])
REFERENCES [dbo].[ciudades] ([id_ciudad])
GO
ALTER TABLE [dbo].[sucursales] CHECK CONSTRAINT [sucursales_FK]
GO
ALTER TABLE [dbo].[transportistas]  WITH CHECK ADD  CONSTRAINT [transportistas_FK] FOREIGN KEY([id_tarifa])
REFERENCES [dbo].[tarifa] ([id_tarifa])
GO
ALTER TABLE [dbo].[transportistas] CHECK CONSTRAINT [transportistas_FK]
GO
ALTER TABLE [dbo].[usuarios]  WITH CHECK ADD  CONSTRAINT [usuarios_FK] FOREIGN KEY([id_colaborador])
REFERENCES [dbo].[colaboradores] ([id_colaborador])
GO
ALTER TABLE [dbo].[usuarios] CHECK CONSTRAINT [usuarios_FK]
GO
ALTER TABLE [dbo].[viajes_detalle]  WITH CHECK ADD  CONSTRAINT [viajes_detalle_FK] FOREIGN KEY([id_viaje_encabezado])
REFERENCES [dbo].[viajes_encabezado] ([id_viaje_encabezado])
GO
ALTER TABLE [dbo].[viajes_detalle] CHECK CONSTRAINT [viajes_detalle_FK]
GO
ALTER TABLE [dbo].[viajes_detalle]  WITH CHECK ADD  CONSTRAINT [viajes_detalle_FK_1] FOREIGN KEY([id_colaborador])
REFERENCES [dbo].[colaboradores] ([id_colaborador])
GO
ALTER TABLE [dbo].[viajes_detalle] CHECK CONSTRAINT [viajes_detalle_FK_1]
GO
ALTER TABLE [dbo].[viajes_detalle]  WITH CHECK ADD  CONSTRAINT [viajes_detalle_FK_2] FOREIGN KEY([id_asignacion])
REFERENCES [dbo].[asignaciones] ([id_asignacion])
GO
ALTER TABLE [dbo].[viajes_detalle] CHECK CONSTRAINT [viajes_detalle_FK_2]
GO
ALTER TABLE [dbo].[viajes_encabezado]  WITH CHECK ADD  CONSTRAINT [viajes_encabezado_FK] FOREIGN KEY([id_colaborador])
REFERENCES [dbo].[colaboradores] ([id_colaborador])
GO
ALTER TABLE [dbo].[viajes_encabezado] CHECK CONSTRAINT [viajes_encabezado_FK]
GO
ALTER TABLE [dbo].[viajes_encabezado]  WITH CHECK ADD  CONSTRAINT [viajes_encabezado_FK_1] FOREIGN KEY([id_transportista])
REFERENCES [dbo].[transportistas] ([id_transportista])
GO
ALTER TABLE [dbo].[viajes_encabezado] CHECK CONSTRAINT [viajes_encabezado_FK_1]
GO
USE [master]
GO
ALTER DATABASE [siman] SET  READ_WRITE 
GO
